package gov.epa.eis.batch.frs.step;

import gov.epa.eis.batch.frs.config.FrsBulkUploadStatusConfiguration;
import gov.epa.eis.batch.frs.webservice.BulkUploadStatusResponse;
import gov.epa.eis.batch.frs.webservice.FrsRequestResponsePayload;
import gov.epa.eis.batch.frs.webservice.FrsWebService;
import gov.epa.eis.model.webservice.TransactionStatus;
import gov.epa.eis.model.webservice.WebServiceTransaction;
import gov.epa.eis.service.WebServiceTransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by DDelVecc on 4/10/2019.
 */
@Component
public class BulkUploadStatusWebServiceStep implements Tasklet {
    private static final Log LOG = LogFactory.getLog(BulkUploadStatusWebServiceStep.class);

    @Autowired
    private WebServiceTransactionService webServiceTransactionService;

    @Autowired
    private FrsWebService frsWebService;
    
    @Autowired
    FrsBulkUploadStatusConfiguration frsBulkUploadStatusConfiguration;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        try {
               LOG.info("BulkUploadStatusWebServiceStep: BULK-UPLOADSTATUS-CHECK is performing a check in the Web Service Transaction Table to find the transaction id of the"
               		+ "last completed transaction and then will make GET request from FrsWebService using the same tx id ");
            //get transaction id from job parameters...
            String lastTransactionId = chunkContext.getStepContext().getStepExecution().getJobParameters().getString("transactionId");

            WebServiceTransaction webServiceTransaction = new WebServiceTransaction();
            webServiceTransaction.setRunTime(new Date());
            
            //  bulk upload status type is getting set here not the buld upload
            webServiceTransaction.setWebServiceType(FrsBulkUploadStatusConfiguration.JOB_NAME);
            webServiceTransaction.setStatus(TransactionStatus.PROCESSING);
            webServiceTransaction.setTransactionId(lastTransactionId);
            webServiceTransactionService.create(webServiceTransaction);


            
            LOG.info("BulkUploadStatusWebServiceStep: BULK-UPLOADSTATUS-CHECK now performs the GET request. The request is made from FrsWebService");
            BulkUploadStatusResponse bulkUploadStatusResponse = frsWebService.getBulkUploadStatus(lastTransactionId);

            //get request response payload
            final FrsRequestResponsePayload frsRequestResponsePayload = bulkUploadStatusResponse.getFrsRequestResponsePayload();

            webServiceTransaction.setCompletedTime(new Date());
//            webServiceTransaction.setNextRunTime(getNextRunDate());
            webServiceTransaction.setTransactionId(bulkUploadStatusResponse.getTransactionId());
            webServiceTransaction.setStatus(bulkUploadStatusResponse.getStatus().equals("PROCESSED") ? TransactionStatus.COMPLETED : TransactionStatus.PROCESSING);

            //load HTTP Request/Response payload info into web service transaction
            frsRequestResponsePayload.load(webServiceTransaction);



            webServiceTransactionService.update(webServiceTransaction);

            //if we get a status other than PROCESSED or PENDING, then throw an error, since something didn't work correctly
            if (!bulkUploadStatusResponse.getStatus().equals("PROCESSED")
                    && !bulkUploadStatusResponse.getStatus().equals("PENDING")
                    && !bulkUploadStatusResponse.getStatus().equals("STARTED")
                    && !bulkUploadStatusResponse.getStatus().equals("VALIDATED")
                    && !bulkUploadStatusResponse.getStatus().equals("CREATED")
            )
                throw new Exception(FrsBulkUploadStatusConfiguration.JOB_NAME + " job, BulkUploadStatusWebServiceStep failed with unknown response status, " + bulkUploadStatusResponse.getStatus());

            contribution.setExitStatus(ExitStatus.COMPLETED);

            LOG.info("Job "+ FrsBulkUploadStatusConfiguration.JOB_NAME + ", completing step");

        }catch(Exception e) {

            contribution.setExitStatus(ExitStatus.FAILED);
            frsBulkUploadStatusConfiguration.sendMail(e.toString());
            LOG.error("Job "+ FrsBulkUploadStatusConfiguration.JOB_NAME + ", failed with exception ",e);
            throw e;    //continue to propagate error

        } finally{
            chunkContext.setComplete();
        }

        return RepeatStatus.FINISHED;
    }
}
