package gov.epa.eis.batch.frs.listener;

import gov.epa.eis.batch.frs.config.FrsBulkUploadConfiguration;
import gov.epa.eis.batch.frs.config.FrsBulkUploadStatusConfiguration;
import gov.epa.eis.model.webservice.WebServiceTransaction;
import gov.epa.eis.service.ApplicationPropertyService;
import gov.epa.eis.service.JobService;
import gov.epa.eis.service.MailService;
import gov.epa.eis.service.WebServiceTransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;


/**
 * The Class FrsBulkUploadStatusJobListener
 *
 * @author Darin
 */
@Component
public class FrsBulkUploadStatusJobListener extends JobExecutionListenerSupport {
	private Date startTime, stopTime;

	@Autowired
	private JobService jobService;

	 @Autowired
	 FrsBulkUploadStatusConfiguration frsBulkUploadStatusConfiguration;

	private static final Log LOG = LogFactory.getLog(FrsBulkUploadStatusJobListener.class);

	@Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = new Date();

        LOG.info(FrsBulkUploadStatusConfiguration.JOB_NAME + " JOB ID:"+jobExecution.getJobId()+" starting at :"+startTime);

		jobService.jobStarted(FrsBulkUploadStatusConfiguration.JOB_NAME, getNextRunDate());

    }

	@Override
	public void afterJob(JobExecution jobExecution) {
		final String jobName = FrsBulkUploadStatusConfiguration.JOB_NAME;
		stopTime = new Date();
		LOG.info(jobName + " JOB ID:" + jobExecution.getJobId() + " stopped at :" + stopTime);
		LOG.info("Total time take in millis :" + getTimeInMillis(startTime, stopTime));

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

			LOG.info(jobName + " job completed successfully");

			jobService.jobCompleted(jobName, getNextRunDate());


		//All exceptions from various Steps will get thrown to here...
		}else if(jobExecution.getStatus() == BatchStatus.FAILED) {
			final StringBuffer exceptions = new StringBuffer();

			jobExecution.getAllFailureExceptions().forEach(ex -> {
				exceptions.append((exceptions.length() == 0 ? "" : ", ") + "Exception:" + ex.getLocalizedMessage());
			});
			LOG.error(jobName + ", job failed with the following exceptions: " + exceptions.toString());

			jobService.jobMisfired(jobName, getNextRunDate());

			
			frsBulkUploadStatusConfiguration.sendMail(exceptions.toString());

		} else {

			LOG.info(jobName + " job completed with status:" + jobExecution.getStatus());
			final StringBuffer exceptions = new StringBuffer();
			jobExecution.getAllFailureExceptions().forEach(ex -> {
				exceptions.append((exceptions.length() == 0 ? "" : ", ") + "Exception:" + ex.getLocalizedMessage());
			});
			LOG.error(jobName + ", job had the following exceptions: " + exceptions.toString());

			jobService.jobMisfired(jobName, getNextRunDate());

			frsBulkUploadStatusConfiguration.sendMail(exceptions.toString());
		}

	}

	private long getTimeInMillis(Date start, Date stop){
        return stop.getTime() - start.getTime();
    }

	private Date getNextRunDate() {
		String cron = FrsBulkUploadStatusConfiguration.CRON_EXPRESSION;
		CronExpression cronExpression = null;
		try {
			cronExpression = new CronExpression(cron);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cronExpression.getNextValidTimeAfter(new Date());
	}

	
}
