package gov.epa.eis.batch.frs.writer;

import gov.epa.eis.model.view.AlternativeFacilityNameCaerFrsView;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 6/27/2018.
 */
@Component
public class JsonFileItemWriter<T> extends FlatFileItemWriter<T> implements StepExecutionListener {

    public JsonFileItemWriter() {
//        lineAggregator = new JsonLineAggregator<>();
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
//        lineAggregator.setObjectMapper(objectMapper);
        super.setLineAggregator(new JsonLineAggregator<T>());
        super.setAppendAllowed(true);
        super.setForceSync(true);
        super.setTransactional(true);
    }

    public void setOutputFilePath(String jsonOutputFilePath) {
        super.setResource(new FileSystemResource(jsonOutputFilePath));
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        //reinitialize Aggregator, VERY important to keep JSON format consistent
        this.setLineAggregator(new JsonLineAggregator<T>());
        setOutputFilePath((String) stepExecution.getJobParameters().getParameters().get("jsonOutputFilePath").getValue());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
