package gov.epa.eis.batch.config.xmlsnapshot;

import gov.epa.eis.batch.xmlsnapshot.writer.CERSXmlHeaderCallback;
import gov.epa.eis.dao.CurrentYearFinder;
import gov.epa.eis.model.AgencyPreferredSystem;
import gov.epa.eis.model.DataCategory;
import gov.epa.eis.model.DataSetIdentifier;
import gov.epa.eis.model.ProgramSystemCode;
import gov.epa.eis.model.report.*;
import gov.epa.eis.service.AgencyOrganizationService;
import gov.epa.eis.service.JobService;
import gov.epa.eis.service.ReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = {"gov.epa.eis.batch.xmlsnapshot.*"})
public class XmlSnapshotJobScheduler {
    private static final Log LOG = LogFactory.getLog(XmlSnapshotJobScheduler.class);
    private static final DataCategory EVENT = DataCategory.EVENT;

    @Autowired
    private Job eventSnapshotJob;

    @Autowired
    private Job areaSnapshotJob;

    @Autowired
    private Job pointEmissionSnapshotJob;

    @Autowired
    private Job facilitySnapshotJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobService jobService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private AgencyOrganizationService agencyOrganizationService;

    @Autowired
    private CurrentYearFinder currentYearFinder;

    @Autowired
    private CERSXmlHeaderCallback xmlHeaderCallback;

//    @Autowired
//    private CERSXmlHeaderCallback cersXmlHeaderCallback;

    //reuse for all snapshots, run in a serial fashion for now...
    //generalize reader, pass in sql
    //generalize writer,
    //do we need config on sub config file...
    @Scheduled(fixedDelay=300000)
    public void run() throws Exception {
        //LOG.info("Job Started at :" + new Date());
        jobService.jobStarted("xmlsnapshot",null);
        //final ReportRequest
        //set up bean for use by reader, writer, etc...
        ReportRequest reportRequest = getReportRequest();
        if (reportRequest == null) {
            LOG.info("No job to run");
            jobService.jobCompleted("xmlsnapshot", new Date(System.currentTimeMillis() + 5 * 60 * 1000));
            return;
        }

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("JobID",
                String.valueOf(System.currentTimeMillis()));
        Long reportRequestId = reportRequest.getId();
        jobParametersBuilder.addLong("ReportRequestId",
                reportRequestId);

        LOG.info("Job to run, reportRequestId=" + reportRequestId);

        //get report request data set and agency Id, which help determine will give us the inventory year and psc
        DataSetIdentifier dataSet = null;
        for (ReportRequestDataSet reportRequestDataSet : reportRequest.getDataSets()) {
            dataSet = reportRequestDataSet.getDataSource();
        }
        Long agencyId = null;
        for (ReportRequestAgency reportRequestAgency : reportRequest.getAgencies()) {
            agencyId = reportRequestAgency.getAgency().getId();
        }
        Long dataSetId = (dataSet != null ? dataSet.getId() : null);
        jobParametersBuilder.addLong("dataSetId",
                dataSetId);
        String inventoryYear = (dataSet != null ? dataSet.getInventoryYear() : currentYearFinder.findCurrentInventoryYear()) + "";
        jobParametersBuilder.addString("InventoryYear",
                inventoryYear);
        String programSystemCode = determineProgramSystemCode(dataSet, agencyId);
        jobParametersBuilder.addString("ProgramSystemCode",
                programSystemCode);

        //get DataCategory
        DataCategory dataCategory = getDataCategory(reportRequest, reportRequest.getReportCode().getCode());

        File reportOutputFile = null;
//        CERSXmlHeaderCallback xmlHeaderCallback = null;
        JobParameters jobParameters = null;
        try {
            String fileName = "";
            //create file name for emissions based snapshots...
            if (dataSet != null)
                fileName = createFileName(dataCategory, dataSet, inventoryYear);
                //create file name for facility inventory snapshot...
            else
                fileName = String.format("Inventory_Snapshot_%s", programSystemCode) + ".xml";

            //convert XML Snapshot file to output and store as Clob
            reportOutputFile = getTempXmlFile(fileName);

            FileSystemResource fileSystemResource = new FileSystemResource(reportOutputFile.getAbsolutePath());

            LOG.info("Temp Output File, ReportRequestOutputFilePath=" + reportOutputFile.getAbsolutePath());

            jobParametersBuilder.addString("ReportRequestOutputFilePath", reportOutputFile.getAbsolutePath());
            jobParameters = jobParametersBuilder.toJobParameters();

            LOG.info("Job "+ jobParameters.getString("JobID") + " Report Request " + jobParameters.getLong("ReportRequestId") + ", starting job");

            Job job = null;
            String reportCode = reportRequest.getReportCode().getCode();
            if (reportCode.equals(ReportCode.ReportCodeType.EVENT_EMIS_SNAPSHOT.getCode())) {
                job = eventSnapshotJob;
            } else if (reportCode.equals(ReportCode.ReportCodeType.AREA_EMIS_SNAPSHOT.getCode())) {
                job = areaSnapshotJob;
            } else if (reportCode.equals(ReportCode.ReportCodeType.POINT_EMIS_SNAPSHOT.getCode())) {
                job = pointEmissionSnapshotJob;
            } else if (reportCode.equals(ReportCode.ReportCodeType.FAC_SNAPSHOT.getCode())) {
                job = facilitySnapshotJob;
            }


            //ticket about validating file size
            //work on testing Area on staging
            //return power supply........


            JobExecution execution = jobLauncher.run(job, jobParameters);

//            //convert XML Snapshot file to output and store as Clob
//            reportService.saveReportRequestOutput(reportRequestId, reportOutputFile);
//
//            //clean up stuff and set completeStatus to complete "1"
//            reportService.cleanUpReportRequestDetails(reportRequestId);
//
//            LOG.info("Job " + jobParameters.getString("JobID") + " Report Request " + jobParameters.getLong("ReportRequestId") + ", finished with status :" + execution.getStatus() + " in " + (System.currentTimeMillis() - Long.valueOf(jobParameters.getString("JobID"))) / 1000.0 + " seconds");


        }catch(JobExecutionAlreadyRunningException |JobRestartException |JobInstanceAlreadyCompleteException |JobParametersInvalidException | RuntimeException e) {
            LOG.error("Job "+ jobParameters.getString("JobID") + " Report Request "+ jobParameters.getLong("ReportRequestId") + ", failed with exception ",e);

            //set completeStatus to failed "2"
            reportService.updateCompleteStatus(reportRequestId, "2");
            jobService.jobMisfired("xmlsnapshot", new Date(System.currentTimeMillis()+5*60*1000));

        } finally{

//            if (reader != null)
//                reader.close();
//            if (writer != null)
//                writer.close();
//            if (reportOutputFile != null)
//                reportOutputFile.delete();
//
//            jobService.jobCompleted("xmlsnapshot", new Date(System.currentTimeMillis()+5*60*1000));
        }
    }

    private ReportRequest getReportRequest() throws Exception {
        LOG.info("Fetching XML Snapshot report requests");
        for (ReportRequest reportRequest : reportService.getJavaRunReportRequests()) {
            if (ReportCode.ReportCodeType.EVENT_EMIS_SNAPSHOT.getCode().equals(reportRequest.getReportCode().getCode())
                    || ReportCode.ReportCodeType.AREA_EMIS_SNAPSHOT.getCode().equals(reportRequest.getReportCode().getCode())
                    || ReportCode.ReportCodeType.POINT_EMIS_SNAPSHOT.getCode().equals(reportRequest.getReportCode().getCode())
                    || ReportCode.ReportCodeType.FAC_SNAPSHOT.getCode().equals(reportRequest.getReportCode().getCode())) {
                return reportRequest;
            }
        }
        return null;
    }

    private File getTempXmlFile(String fileName) throws IOException {
        File file = null;

        //define temporary XML file to write too
        Path path = Files.createTempFile(fileName, null);
        file = path.toFile();
        // This tells JVM to delete the file on JVM exit.
        // Useful for temporary files in tests.
        file.deleteOnExit();
        return file;
    }


    private DataCategory getDataCategory(ReportRequest reportRequest, final String reportTypeCode) {
        if (reportTypeCode.equals(ReportCode.ReportCodeType.EVENT_EMIS_SNAPSHOT.getCode())) {
            return EVENT;
        } else if (reportTypeCode.equals(ReportCode.ReportCodeType.AREA_EMIS_SNAPSHOT.getCode())) {
            for (ReportRequestDataCategory reportRequestDataCategory : reportRequest.getDataCategories()) {
                return reportRequestDataCategory.getDataCategory();
            }
            return DataCategory.NONPOINT;
        } else if (reportTypeCode.equals(ReportCode.ReportCodeType.POINT_EMIS_SNAPSHOT.getCode())) {
            return DataCategory.POINT;
        } else if (reportTypeCode.equals(ReportCode.ReportCodeType.FAC_SNAPSHOT.getCode())) {
            return DataCategory.FACILITY;
        }
        return null;
    }

    private String determineProgramSystemCode(final DataSetIdentifier dataSetIdentifier, final Long agencyId) {
        String pscCode = "";
        if (dataSetIdentifier != null && dataSetIdentifier.getAgencyId() == null) {

            pscCode = ProgramSystemCode.EIS_CODE;

        } else if (dataSetIdentifier != null && dataSetIdentifier.getAgencyId() != null) {
            pscCode = determineProgramSystemCode(dataSetIdentifier.getAgencyId());

        } else if (agencyId != null) {
            pscCode = determineProgramSystemCode(agencyId);
        }

        return pscCode;
    }


    private String determineProgramSystemCode(final Long agencyId) {
        String pscCode = "";

        List<AgencyPreferredSystem> systems = agencyOrganizationService.findPreferredSystemsOfAgency(agencyId);

        //use FI as the target data category
        if (!systems.isEmpty()) {
            for (AgencyPreferredSystem agencyPreferredSystem : systems) {
                //only use FI designated
                if (agencyPreferredSystem.getDataCategory().equals(DataCategory.FACILITY))
                    pscCode = agencyPreferredSystem.getProgramSystemCode().getCode();
            }
        }
        return pscCode;
    }

    public String createFileName(final DataCategory category,
                                 final DataSetIdentifier identifier,
                                 final String inventoryYear) {
        String fileName = "";
        fileName = String.format("%s_Emissions_%s_%s.xml",
                category.getDescription(), inventoryYear, identifier.getDataSetName().trim());
        return removeIllegalCharacters(fileName);
    }

    private String removeIllegalCharacters(final String fileName) {
        // Replace illegal Windows filename characters ("\/:*?<>| ) with underscores.
        String result = fileName.replaceAll("[\"\\/\\*\\?\\s\\\\\\|:<>]", "_");

        // Truncate long file names
        if (result.length() > MAX_FILENAME_LENGTH) {
            result = result.substring(0, MAX_FILENAME_LENGTH - 4) + ".xml";
        }
        return result;
    }

    // Windows chokes on file names longer than 256 characters. Since this path is included
    // in the length determination, reserve 80 characters for the path
    private static final int MAX_FILENAME_LENGTH = 175;

}