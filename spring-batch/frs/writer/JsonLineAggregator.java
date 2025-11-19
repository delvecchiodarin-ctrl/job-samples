package gov.epa.eis.batch.frs.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.io.IOException;

/**
 * Created by DDelVecc on 3/28/2019.
 */
public class JsonLineAggregator<T> implements LineAggregator<T> {

    private ObjectMapper objectMapper = new ObjectMapper();

//    private Gson gson = new Gson();
    private boolean isFirstObject = true;

    public JsonLineAggregator() {
        // First: need a custom serializer provider
        DefaultSerializerProvider sp = new DefaultSerializerProvider.Impl();
        sp.setNullValueSerializer(new NullSerializer());
        // And then configure mapper to use it
        objectMapper.setSerializerProvider(sp);
        // Serialization as done using regular ObjectMapper.writeValue()
    }

    @Override
    public String aggregate(final T item) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
 //           e.printStackTrace();
        }
        if (isFirstObject) {
            isFirstObject = false;
            return json;
        }
        return "," + json;
    }
}
