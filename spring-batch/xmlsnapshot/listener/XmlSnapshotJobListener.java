package gov.epa.eis.batch.xmlsnapshot.listener;

import gov.epa.eis.service.JobService;
import gov.epa.eis.service.ReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.List;


/**
 * The Class XmlSnapshotJobListener
 *
 * @author Darin
 */
@Component
public class XmlSnapshotJobListener extends JobExecutionListenerSupport {
	private Date startTime, stopTime;

	@Autowired
	private ReportService reportService;
	@Autowired
	private JobService jobService;

	private static final Log LOG = LogFactory.getLog(XmlSnapshotJobListener.class);
	 @Override
    public void beforeJob(JobExecution jobExecution) {
		JobParameters jobParameters = jobExecution.getJobParameters();
		final Long reportRequestId = jobParameters.getLong("ReportRequestId");

        startTime = new Date();
        LOG.info("JOB ID:"+jobExecution.getJobId()+" starting at :"+startTime);

		 //change this flag to running, this will make sure batch doesn't try to run again
		reportService.updateJavaRunStatus(reportRequestId, 2);
    }

	@Override
	public void afterJob(JobExecution jobExecution) {
		stopTime = new Date();
		LOG.info("JOB ID:"+jobExecution.getJobId()+" stopped at :"+stopTime);
		LOG.info("Total time take in millis :"+getTimeInMillis(startTime , stopTime));

		JobParameters jobParameters = jobExecution.getJobParameters();
		final Long reportRequestId = jobParameters.getLong("ReportRequestId");
		final String reportRequestOutputFilePath = jobParameters.getString("ReportRequestOutputFilePath");
		File outputFile = new File(reportRequestOutputFilePath);

        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
        	LOG.info("xmlsnapshot job completed successfully");
            //Here you can perform some other business logic like cleanup

					//other clean up, close report, store report...
					//convert XML Snapshot file to output and store as Clob
					reportService.saveReportRequestOutput(reportRequestId, outputFile);

					//clean up stuff and set completeStatus to complete "1"
					reportService.cleanUpReportRequestDetails(reportRequestId);


					jobService.jobCompleted("xmlsnapshot", new Date(System.currentTimeMillis() + 5 * 60 * 1000));

		}else if(jobExecution.getStatus() == BatchStatus.FAILED){
        	LOG.info("XmlSubmissionJob job failed with following exceptions ");
            List<Throwable> exceptionList = jobExecution.getAllFailureExceptions();
            for(Throwable th : exceptionList){
            	LOG.error("exception :" +th.getLocalizedMessage());
            }

			//set completeStatus to failed "2"
			reportService.updateCompleteStatus(reportRequestId, "2");
			jobService.jobMisfired("xmlsnapshot", new Date(System.currentTimeMillis() + 5 * 60 * 1000));


		}else {
        	LOG.info("XmlSubmissionJob job completed with status:"+jobExecution.getStatus());

			//set completeStatus to failed "2"
			reportService.updateCompleteStatus(reportRequestId, "2");
			jobService.jobMisfired("xmlsnapshot", new Date(System.currentTimeMillis() + 5 * 60 * 1000));

		}

		if (outputFile != null)
			outputFile.delete();

	}
	private long getTimeInMillis(Date start, Date stop){
        return stop.getTime() - start.getTime();
    }
}
