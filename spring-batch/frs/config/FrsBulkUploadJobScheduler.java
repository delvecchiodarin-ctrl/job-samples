package gov.epa.eis.batch.config.frs;

import gov.epa.eis.batch.frs.config.FrsBulkUploadConfiguration;
import gov.epa.eis.batch.frs.config.FrsBulkUploadStatusConfiguration;
import gov.epa.eis.model.webservice.WebServiceTransaction;
import gov.epa.eis.service.JobService;
import gov.epa.eis.service.WebServiceTransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = {"gov.epa.eis.batch.frs.*"})
public class FrsBulkUploadJobScheduler {
    private static final Log LOG = LogFactory.getLog(FrsBulkUploadJobScheduler.class);

    @Autowired
    private Job frsBulkUploadJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private WebServiceTransactionService webServiceTransactionService;

    @Autowired
    private JobService jobService;

//    @Scheduled(cron=FrsBulkUploadConfiguration.CRON_EXPRESSION)
    public void run() throws Exception {
        LOG.info("FrsBulkUploadJobScheduler:Started the Bulk Upload Job:"+FrsBulkUploadStatusConfiguration.JOB_NAME);
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("JobID",
                String.valueOf(System.currentTimeMillis()))
                .addDate("date", new Date())
                .addLong("time", System.currentTimeMillis());

        //Let's see if we even need to run the job...
        //see if there is a bulk upload already being processed by Frs...
        LOG.info("Will need to check if there is bulk upload in COMPLETED state");
        WebServiceTransaction lastCompletedWebServiceTransaction = webServiceTransactionService.
                getLastNotProcessedFrsBulkUploadWebServiceTransaction(FrsBulkUploadConfiguration.JOB_NAME,
                        FrsBulkUploadStatusConfiguration.JOB_NAME);
        if (lastCompletedWebServiceTransaction != null) {	//nothing to do, system is still checking the status of a previous BulkUpload

            LOG.info("Job "+ FrsBulkUploadConfiguration.JOB_NAME + " will not run, no facilities to push");
            return; //NOTHING TO RUN

        }

        File jsonFile = null;
        JobParameters jobParameters = null;
        try {

            //get temporary json file
            jsonFile = getTempFile();


            //figure out last pull date from Job table, get lastStartTime
            Date lastPullDate = jobService.getJob(FrsBulkUploadConfiguration.JOB_NAME).getLastStartTime();  //new Date(2019 - 1900, 3, 6 );

            jobParametersBuilder.addString("jsonOutputFilePath", jsonFile.getAbsolutePath());
            jobParametersBuilder.addDate("lastPullDate", lastPullDate);

            jobParameters = jobParametersBuilder.toJobParameters();

            LOG.info("Job "+ jobParameters.getString("JobID") + ", starting job");

            //kicked off asynchronously, all begin/end job logic is handled in Listener
            JobExecution jobExecution = jobLauncher.run(frsBulkUploadJob, jobParameters);

        }catch(JobExecutionAlreadyRunningException |JobRestartException |JobInstanceAlreadyCompleteException |JobParametersInvalidException | RuntimeException e) {

            LOG.error("Job "+ jobParameters.getString("JobID") + ", " + FrsBulkUploadConfiguration.JOB_NAME + ", failed with exception ",e);

        } finally{
            //
        }
    }

    private File getTempFile() throws IOException {
        File file = null;

        //define temporary XML file to write too
        Path path = Files.createTempFile("frs_push.json", null);
        file = path.toFile();
        // This tells JVM to delete the file on JVM exit.
        // Useful for temporary files in tests.
        file.deleteOnExit();
        return file;
    }
}