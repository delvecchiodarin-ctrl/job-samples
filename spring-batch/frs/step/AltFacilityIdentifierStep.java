package gov.epa.eis.batch.frs.step;

import gov.epa.eis.batch.frs.reader.AltFacilityIdentifierItemReader;
import gov.epa.eis.batch.frs.writer.AltFacilityIdentifierItemWriter;
import gov.epa.eis.model.view.AlternativeFacilityIdCaerFrsView;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/10/2019.
 */
@Component
public class AltFacilityIdentifierStep {

    private StepBuilderFactory stepBuilderFactory;
    private AltFacilityIdentifierItemReader altFacilityIdentifierItemReader;
    private AltFacilityIdentifierItemWriter altFacilityIdentifierItemWriter;

    @Autowired
    public AltFacilityIdentifierStep(StepBuilderFactory stepBuilderFactory,
                                     AltFacilityIdentifierItemReader altFacilityIdentifierItemReader,
                                     AltFacilityIdentifierItemWriter altFacilityIdentifierItemWriter) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.altFacilityIdentifierItemReader = altFacilityIdentifierItemReader;
        this.altFacilityIdentifierItemWriter = altFacilityIdentifierItemWriter;
    }

    public TaskletStep getTaskletStep() {
        return stepBuilderFactory.get(AltFacilityIdentifierStep.class.getName())
                .<AlternativeFacilityIdCaerFrsView, AlternativeFacilityIdCaerFrsView> chunk(10)
                .reader(altFacilityIdentifierItemReader)
                .writer(altFacilityIdentifierItemWriter)
                .build();
    }

}
