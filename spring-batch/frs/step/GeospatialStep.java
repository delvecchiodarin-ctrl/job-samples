package gov.epa.eis.batch.frs.step;

import gov.epa.eis.batch.frs.reader.FacilityItemReader;
import gov.epa.eis.batch.frs.reader.GeospatialItemReader;
import gov.epa.eis.batch.frs.writer.FacilityItemWriter;
import gov.epa.eis.batch.frs.writer.GeospatialItemWriter;
import gov.epa.eis.model.view.FacilitySiteCaerFrsView;
import gov.epa.eis.model.view.GeospatialCaerFrsView;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/10/2019.
 */
@Component
public class GeospatialStep {

    private StepBuilderFactory stepBuilderFactory;
    private GeospatialItemReader geospatialItemReader;
    private GeospatialItemWriter geospatialItemWriter;

    @Autowired
    public GeospatialStep(StepBuilderFactory stepBuilderFactory,
                          GeospatialItemReader geospatialItemReader,
                          GeospatialItemWriter geospatialItemWriter) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.geospatialItemReader = geospatialItemReader;
        this.geospatialItemWriter = geospatialItemWriter;
    }

    public TaskletStep getTaskletStep() {
        return stepBuilderFactory.get(GeospatialStep.class.getName())
                .<GeospatialCaerFrsView, GeospatialCaerFrsView> chunk(50)
                .reader(geospatialItemReader)
//                .processor(itemProcessor)
                .writer(geospatialItemWriter)
                .build();
    }
}
