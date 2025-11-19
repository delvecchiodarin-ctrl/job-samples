package gov.epa.eis.batch.frs.step;

import gov.epa.eis.batch.frs.config.FrsBulkUploadConfiguration;
import gov.epa.eis.batch.frs.webservice.BulkUploadResponse;
import gov.epa.eis.batch.frs.webservice.FrsWebService;
import gov.epa.eis.model.webservice.TransactionStatus;
import gov.epa.eis.model.webservice.WebServiceTransaction;
import gov.epa.eis.service.WebServiceTransactionService;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * Created by DDelVecc on 4/10/2019.
 */
@Component
public class BulkUploadWebServiceStep implements Tasklet {
    private static final Log LOG = LogFactory.getLog(BulkUploadWebServiceStep.class);

    @Autowired
    private WebServiceTransactionService webServiceTransactionService;

    @Autowired
    private FrsWebService frsWebService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

            File outputFile = new File(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jsonOutputFilePath"));
            File zipFile = null;
            try {
                //only push if there are some records to process
                if (chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().getLong("readCount") > 0) {
                    BulkUploadResponse bulkUploadResponse = null;

                    // save zip file to web service database table
                    zipFile = buildZipFile(outputFile);

                    // BULK UPLOAD FILE
                    // push zip file using web service call, then get transaction Id
                    WebServiceTransaction webServiceTransaction = new WebServiceTransaction();
                    webServiceTransaction.setRunTime(new Date());
                    webServiceTransaction.setWebServiceType(FrsBulkUploadConfiguration.JOB_NAME);
                    webServiceTransaction.setStatus(TransactionStatus.PROCESSING);
                    webServiceTransactionService.create(webServiceTransaction);
                    try {
                        bulkUploadResponse = frsWebService.bulkUpload(zipFile);
                        if (bulkUploadResponse != null && bulkUploadResponse.getFrsRequestResponsePayload().getStatusCode() == 200) {
                            webServiceTransaction.setTransactionId(bulkUploadResponse.getTransactionId());
                            webServiceTransaction.setStatus(TransactionStatus.COMPLETED);
                        } else {
                            webServiceTransaction.setStatus(TransactionStatus.ERROR_PROCESSING);
                        }
                    } catch (Exception e) {
                        webServiceTransaction.setStatus(TransactionStatus.ERROR_PROCESSING);
                    }

                    // If an exception is thrown bulkUploadResponse will come back as null.
                    // The status will then be reset to ERROR_PROCESSING

                    webServiceTransaction.setCompletedTime(new Date());
                    try {
                        // load file that was posted...
                        if (zipFile != null)
                            webServiceTransaction.setRequestBodyFile(FileUtils.readFileToByteArray(zipFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // load HTTP Request/Response payload info into web service transaction
                    if (bulkUploadResponse != null && bulkUploadResponse.getFrsRequestResponsePayload() != null)
                        bulkUploadResponse.getFrsRequestResponsePayload().load(webServiceTransaction);

                    webServiceTransactionService.update(webServiceTransaction);

                    LOG.info(FrsBulkUploadConfiguration.JOB_NAME + " job completed successfully");
                    contribution.setExitStatus(ExitStatus.COMPLETED);

                    LOG.info("Job "+ FrsBulkUploadConfiguration.JOB_NAME + ", completing step");
                }

            }catch(RuntimeException e) {

                contribution.setExitStatus(ExitStatus.FAILED);
                LOG.error("Job "+ FrsBulkUploadConfiguration.JOB_NAME + ", failed with exception ",e);

            } finally{
                if (outputFile != null)
                    outputFile.delete();
                if (zipFile != null)
                    zipFile.delete();
                chunkContext.setComplete();
            }

        return RepeatStatus.FINISHED;
    }

    private File getTempZipFile() throws IOException {
        File file = null;

        //define temporary XML file to write too
        Path path = Files.createTempFile("frs_push.zip", null);
        file = path.toFile();
        // This tells JVM to delete the file on JVM exit.
        // Useful for temporary files in tests.
        file.deleteOnExit();
        return file;
    }

    private File buildZipFile(File jsonOutputFile) {

        FileOutputStream outputStream = null;
        ArchiveOutputStream archive = null;
        File zipFile = null;
        try {
            zipFile = getTempZipFile();

            outputStream = new FileOutputStream(zipFile);
            archive = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, outputStream);

            ZipArchiveEntry entry = new ZipArchiveEntry("eis_frs_push.json");
            archive.putArchiveEntry(entry);
            FileInputStream fileInputStream = new FileInputStream(jsonOutputFile);
            long blobSize = IOUtils.copy(fileInputStream, archive);
            fileInputStream.close();
            archive.closeArchiveEntry();
            archive.finish();
            archive.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArchiveException e) {
            e.printStackTrace();
        } finally {
            //
        }
        return zipFile;
    }
}
