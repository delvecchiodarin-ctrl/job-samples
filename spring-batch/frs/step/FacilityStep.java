package gov.epa.eis.batch.frs.step;

import gov.epa.eis.batch.frs.writer.FacilityItemWriter;
import gov.epa.eis.batch.frs.reader.FacilityItemReader;
import gov.epa.eis.model.view.FacilitySiteCaerFrsView;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/10/2019.
 */
@Component
public class FacilityStep {

    private StepBuilderFactory stepBuilderFactory;
    private FacilityItemReader facilityItemReader;
    private FacilityItemWriter facilityItemWriter;

    @Autowired
    public FacilityStep(StepBuilderFactory stepBuilderFactory,
                        FacilityItemReader facilityItemReader,
                        FacilityItemWriter facilityItemWriter) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.facilityItemReader = facilityItemReader;
        this.facilityItemWriter = facilityItemWriter;
    }

    public TaskletStep getTaskletStep() {
        return stepBuilderFactory.get(FacilityStep.class.getName())
                .<FacilitySiteCaerFrsView, FacilitySiteCaerFrsView> chunk(20)
                .reader(facilityItemReader)
//                .processor(itemProcessor)
                .writer(facilityItemWriter)
                .build();
    }
}
