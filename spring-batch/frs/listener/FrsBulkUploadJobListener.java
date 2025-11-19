package gov.epa.eis.batch.frs.listener;

import gov.epa.eis.batch.frs.config.FrsBulkUploadConfiguration;
import gov.epa.eis.service.JobService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;


/**
 * The Class FrsBulkUploadJobListener
 *
 * @author Darin
 */
@Component
public class FrsBulkUploadJobListener extends JobExecutionListenerSupport {
	private Date startTime, stopTime;

	@Autowired
	private JobService jobService;

	private static final Log LOG = LogFactory.getLog(FrsBulkUploadJobListener.class);

	private Date originalLastStartDate;

	@Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = new Date();

        LOG.info("FrsBulkUploadJobListener:"+FrsBulkUploadConfiguration.JOB_NAME + " JOB ID:" + jobExecution.getJobId() + " starting at :" + startTime);

		//get this just in case something fails, then we can reset the start time!
		originalLastStartDate = jobService.getJob(FrsBulkUploadConfiguration.JOB_NAME).getLastStartTime();

		jobService.jobStarted(FrsBulkUploadConfiguration.JOB_NAME, getNextRunDate());
    }

	@Override
	public void afterJob(JobExecution jobExecution) {
		final String jobName = FrsBulkUploadConfiguration.JOB_NAME;
		stopTime = new Date();
		LOG.info(jobName + " JOB ID:" + jobExecution.getJobId() + " stopped at :" + stopTime);
		LOG.info("Total time take in millis :" + getTimeInMillis(startTime, stopTime));


		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

			jobService.jobCompleted(jobName, getNextRunDate());

		} else if(jobExecution.getStatus() == BatchStatus.FAILED){
        	LOG.info(jobName + " failed with following exceptions ");
            List<Throwable> exceptionList = jobExecution.getAllFailureExceptions();
            for(Throwable th : exceptionList){
            	LOG.error("exception :" +th.getLocalizedMessage());
            }

			jobService.jobMisfired(jobName, getNextRunDate());
			jobService.resetJobStartTime(jobName, originalLastStartDate);

		} else {

			//mail issues


			LOG.info(jobName + " job completed with status:" + jobExecution.getStatus());
			List<Throwable> exceptionList = jobExecution.getAllFailureExceptions();
			for(Throwable th : exceptionList){
				LOG.error("exception :" +th.getLocalizedMessage());
			}

			jobService.jobMisfired(jobName, getNextRunDate());
			jobService.resetJobStartTime(jobName, originalLastStartDate);
		}

	}

	private long getTimeInMillis(Date start, Date stop){
        return stop.getTime() - start.getTime();
    }

	private Date getNextRunDate() {
		String cron = FrsBulkUploadConfiguration.CRON_EXPRESSION;
		CronExpression cronExpression = null;
		try {
			cronExpression = new CronExpression(cron);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cronExpression.getNextValidTimeAfter(new Date());
	}
}
