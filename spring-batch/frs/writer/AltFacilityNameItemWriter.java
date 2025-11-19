package gov.epa.eis.batch.frs.writer;

import gov.epa.eis.model.view.AlternativeFacilityNameCaerFrsView;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/6/2019.
 */
@Component
public class AltFacilityNameItemWriter extends JsonFileItemWriter<AlternativeFacilityNameCaerFrsView> {

}
