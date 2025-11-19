package gov.epa.eis.batch.frs.writer;

import org.springframework.batch.item.file.FlatFileFooterCallback;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by DDelVecc on 3/28/2019.
 */
public class JsonFlatFileFooterCallback implements FlatFileFooterCallback {

    @Override
    public void writeFooter(final Writer writer) throws IOException {
        writer.write("]");
    }
}