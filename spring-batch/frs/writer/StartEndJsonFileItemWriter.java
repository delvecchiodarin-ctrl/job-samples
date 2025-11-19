package gov.epa.eis.batch.frs.writer;

import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/6/2019.
 */
@Component
public class StartEndJsonFileItemWriter extends JsonFileItemWriter<String> {
    public StartEndJsonFileItemWriter() {
        super();
        this.setLineAggregator(new PassThroughLineAggregator());
        this.setFooterCallback(null);
    }
}