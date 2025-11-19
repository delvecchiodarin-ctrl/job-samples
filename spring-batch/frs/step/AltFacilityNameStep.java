package gov.epa.eis.batch.frs.step;

import gov.epa.eis.batch.frs.reader.AltFacilityNameItemReader;
import gov.epa.eis.batch.frs.writer.AltFacilityNameItemWriter;
import gov.epa.eis.model.view.AlternativeFacilityNameCaerFrsView;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/10/2019.
 */
@Component
public class AltFacilityNameStep {

    private StepBuilderFactory stepBuilderFactory;
    private AltFacilityNameItemReader altFacilityNameItemReader;
    private AltFacilityNameItemWriter altFacilityNameItemWriter;

    @Autowired
    public AltFacilityNameStep(StepBuilderFactory stepBuilderFactory,
                               AltFacilityNameItemReader altFacilityNameItemReader,
                               AltFacilityNameItemWriter altFacilityNameItemWriter) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.altFacilityNameItemReader = altFacilityNameItemReader;
        this.altFacilityNameItemWriter = altFacilityNameItemWriter;
    }

    public TaskletStep getTaskletStep() {
        return stepBuilderFactory.get(AltFacilityNameStep.class.getName())
                .<AlternativeFacilityNameCaerFrsView, AlternativeFacilityNameCaerFrsView> chunk(10)
                .reader(altFacilityNameItemReader)
                .writer(altFacilityNameItemWriter)
                .build();
    }
}
