package gov.epa.eis.batch.frs.config;

import gov.epa.eis.batch.frs.listener.FrsBulkUploadStatusJobListener;
import gov.epa.eis.service.ApplicationPropertyService;
import gov.epa.eis.service.MailService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;

@Configuration
public class FrsBulkUploadStatusConfiguration {
    private static final Log LOG = LogFactory.getLog(FrsBulkUploadStatusConfiguration.class);
    public static final String JOB_NAME = "FRS-BULKUPLOAD-STATUS";
    public static final String CRON_EXPRESSION = "0 0/2 * * * ?";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    

    @Autowired
    private ApplicationPropertyService applicationPropertyService;
    
    @Autowired
    private MailService mailService;

    @Bean
    public Job frsBulkUploadStatusJob(Tasklet bulkUploadStatusWebServiceStep,
                                FrsBulkUploadStatusJobListener frsBulkUploadStatusJobListener) {
   	LOG.info("FrsBulkUploadStatusConfiguration: BULK-UPLOADSTATUS-CHECK is now performing the web service step");
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                //this is where it is getting started 2nd step during the process
                .start(webServiceStep(bulkUploadStatusWebServiceStep))
                .listener(frsBulkUploadStatusJobListener)
                .build();
    }

    private Step webServiceStep(Tasklet bulkUploadStatusWebServiceStep) {
        return stepBuilderFactory.get(bulkUploadStatusWebServiceStep.getClass().getName())
                .tasklet(bulkUploadStatusWebServiceStep)
                .build();
    }
    
    public void sendMail(final String msgText) {
        
        final String mailTo = applicationPropertyService.get("mail.exception.to").getValue();
        final String mailSubject = applicationPropertyService.get("mail.exception.subject").getValue();
        final String mailExceptionFrom = applicationPropertyService.get("mail.exception.from").getValue();

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("srbaral@attain.com");
        msg.setFrom("baral.sachet@gmail.com");
        msg.setSubject(mailSubject + " - Batch Job, " + FrsBulkUploadStatusConfiguration.JOB_NAME);
        msg.setText(msgText);

        mailService.sendMessage(msg);
    }
}