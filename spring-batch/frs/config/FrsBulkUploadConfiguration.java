package gov.epa.eis.batch.frs.config;

import gov.epa.eis.batch.frs.listener.FrsBulkUploadJobListener;
import gov.epa.eis.batch.frs.step.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

//@Lazy
@Configuration
//@EnableBatchProcessing
public class FrsBulkUploadConfiguration {
    private static final Log LOG = LogFactory.getLog(FrsBulkUploadConfiguration.class);
   
    public static final String JOB_NAME = "FRS-BULKUPLOAD";
    public static final String CRON_EXPRESSION = "0 0/2 * * * ?";


    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private FrsBulkUploadJobListener frsBulkUploadJobListener;

    @Bean
    public Job frsBulkUploadJob(Tasklet jsonStartStep,
                                FacilityStep facilityStep,
                                Tasklet jsonStartArrayAltFacilityIdentifierStep,
                                AltFacilityIdentifierStep altFacilityIdentifiersStep,
                                Tasklet jsonStartArrayAltFacilityNameStep,
                                AltFacilityNameStep altFacilityNameStep,
                                Tasklet jsonStartArrayGeospatialStep,
                                GeospatialStep geospatialStep,
                                Tasklet jsonStartArrayNaicsStep,
                                NaicsStep naiscStep,
			                    Tasklet jsonStartArraySubFacilityStep,
                                SubFacilityStep subFacilityStep,
                                Tasklet jsonEndStep,
                                Tasklet jsonStartArrayFacilityStep,
                                Tasklet jsonEndArrayStep,
                                Tasklet bulkUploadWebServiceStep) {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(startStep(jsonStartStep))

                //facility
                .next(startArrayStep(jsonStartArrayFacilityStep))
                .next(facilityStep.getTaskletStep())
                .next(endArrayStep(jsonEndArrayStep))

                //alt facility identifier
                .next(startArrayStep(jsonStartArrayAltFacilityIdentifierStep))
                .next(altFacilityIdentifiersStep.getTaskletStep())
                .next(endArrayStep(jsonEndArrayStep))

                //alt facility name
                .next(startArrayStep(jsonStartArrayAltFacilityNameStep))
                .next(altFacilityNameStep.getTaskletStep())
                .next(endArrayStep(jsonEndArrayStep))

                //Geospatial
                .next(startArrayStep(jsonStartArrayGeospatialStep))
                .next(geospatialStep.getTaskletStep())
                .next(endArrayStep(jsonEndArrayStep))
                
                //NAICS
                .next(startArrayStep(jsonStartArrayNaicsStep))
                .next(naiscStep.getTaskletStep())
                .next(endArrayStep(jsonEndArrayStep))

                //SubFacility
				.next(startArrayStep(jsonStartArraySubFacilityStep))
				.next(subFacilityStep.getTaskletStep())
                .next(endArrayStep(jsonEndArrayStep))
				 
                .next(endStep(jsonEndStep))

                //web service call step
                .next(webServiceStep(bulkUploadWebServiceStep))

                .listener(frsBulkUploadJobListener)
                .build();
    }

    private Step startStep(Tasklet jsonStartStep) {
        return stepBuilderFactory.get(jsonStartStep.getClass().getName())
                .tasklet(jsonStartStep)
                .build();
    }

    private Step endStep(Tasklet jsonEndStep) {
        return stepBuilderFactory.get(jsonEndStep.getClass().getName())
                .tasklet(jsonEndStep)
                .build();
    }

    private Step startArrayStep(Tasklet jsonStartArrayStep) {
        return stepBuilderFactory.get(jsonStartArrayStep.getClass().getName())
                .tasklet(jsonStartArrayStep)
                .build();
    }

    private Step endArrayStep(Tasklet jsonEndArrayStep) {
        return stepBuilderFactory.get(jsonEndArrayStep.getClass().getName())
                .tasklet(jsonEndArrayStep)
                .build();
    }

    private Step webServiceStep(Tasklet bulkUploadWebServiceStep) {
        return stepBuilderFactory.get(bulkUploadWebServiceStep.getClass().getName())
                .tasklet(bulkUploadWebServiceStep)
                .build();
    }

}