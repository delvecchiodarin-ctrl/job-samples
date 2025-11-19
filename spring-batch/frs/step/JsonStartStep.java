package gov.epa.eis.batch.frs.step;

import gov.epa.eis.batch.frs.writer.StartEndJsonFileItemWriter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DDelVecc on 4/10/2019.
 */
@Component
public class JsonStartStep implements Tasklet, StepExecutionListener  {

    @Autowired
    private StartEndJsonFileItemWriter startEndJsonFileItemWriter;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<String> data = new ArrayList<>();
        data.add("{");
        startEndJsonFileItemWriter.write(data);

        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        startEndJsonFileItemWriter.setResource(new FileSystemResource((String) stepExecution.getJobParameters().getParameters().get("jsonOutputFilePath").getValue()));
        startEndJsonFileItemWriter.open(stepExecution.getJobExecution().getExecutionContext());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        startEndJsonFileItemWriter.close();
        return ExitStatus.COMPLETED;
    }
}
