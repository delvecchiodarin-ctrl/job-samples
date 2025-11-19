package gov.epa.eis.batch.frs.step;

import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/10/2019.
 */
@Component
public class JsonStartArrayAltFacilityNameStep extends JsonStartArrayStep {

    public JsonStartArrayAltFacilityNameStep() {
        setFieldName("\"alternativenames\"");
    }
}
