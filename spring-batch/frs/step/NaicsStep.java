package gov.epa.eis.batch.frs.step;

import gov.epa.eis.batch.frs.reader.NaicsItemReader;
import gov.epa.eis.batch.frs.writer.NaicsItemWriter;
import gov.epa.eis.model.view.NaicsCareFrsView;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/10/2019.
 */
@Component
public class NaicsStep {

    private StepBuilderFactory stepBuilderFactory;
    private NaicsItemReader naicsItemReader;
    private NaicsItemWriter naicsItemWriter;

    @Autowired
    public NaicsStep(StepBuilderFactory stepBuilderFactory,
    		        NaicsItemReader naicsItemReader,
    		        NaicsItemWriter naicsItemWriter) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.naicsItemReader = naicsItemReader;
        this.naicsItemWriter = naicsItemWriter;
    }

    public TaskletStep getTaskletStep() {
        return stepBuilderFactory.get(NaicsStep.class.getName())
                .<NaicsCareFrsView, NaicsCareFrsView> chunk(20)
                .reader(naicsItemReader)
//                .processor(itemProcessor)
                .writer(naicsItemWriter)
                .build();
    }
}
