package gov.epa.eis.batch.frs.step;

import gov.epa.eis.batch.frs.reader.SubFacilityItemReader;
import gov.epa.eis.batch.frs.writer.SubFacilityItemWriter;
import gov.epa.eis.model.view.SubFacilityCaerFrsView;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/10/2019.
 */
@Component
public class SubFacilityStep {

    private StepBuilderFactory stepBuilderFactory;
    private SubFacilityItemReader subFacilityItemReader;
    private SubFacilityItemWriter subFacilityItemWriter;

    @Autowired
    public SubFacilityStep(StepBuilderFactory stepBuilderFactory,
                           SubFacilityItemReader subFacilityItemReader,
                           SubFacilityItemWriter subFacilityItemWriter) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.subFacilityItemReader = subFacilityItemReader;
        this.subFacilityItemWriter = subFacilityItemWriter;
    }

    public TaskletStep getTaskletStep() {
        return stepBuilderFactory.get(SubFacilityStep.class.getName())
                .<SubFacilityCaerFrsView, SubFacilityCaerFrsView> chunk(20)
                .reader(subFacilityItemReader)
//                .processor(itemProcessor)
                .writer(subFacilityItemWriter)
                .build();
    }
}
