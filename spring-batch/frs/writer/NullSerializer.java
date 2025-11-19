package gov.epa.eis.batch.frs.writer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by DDelVecc on 5/7/2019.
 */
// and NullSerializer can be something as simple as:
public class NullSerializer extends JsonSerializer<Object>
{
    @Override
    public void serialize(Object object, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        // any JSON value you want...
        jsonGenerator.writeString("");
    }
}
