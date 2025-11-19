package gov.epa.eis.batch.frs.writer;

import gov.epa.eis.model.view.FacilitySiteCaerFrsView;
import gov.epa.eis.model.view.GeospatialCaerFrsView;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/6/2019.
 */
@Component
public class GeospatialItemWriter extends JsonFileItemWriter<GeospatialCaerFrsView> {

}
