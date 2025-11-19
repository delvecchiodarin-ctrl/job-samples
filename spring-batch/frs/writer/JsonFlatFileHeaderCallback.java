package gov.epa.eis.batch.frs.writer;

import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by DDelVecc on 3/28/2019.
 */
public class JsonFlatFileHeaderCallback implements FlatFileHeaderCallback {

    private String fieldName;

    public JsonFlatFileHeaderCallback(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void writeHeader(Writer writer) throws IOException {
        writer.write(fieldName + ": [");

    }
}