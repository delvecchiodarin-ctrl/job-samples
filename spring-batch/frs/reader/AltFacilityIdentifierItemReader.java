package gov.epa.eis.batch.frs.reader;

import gov.epa.eis.model.view.AlternativeFacilityIdCaerFrsView;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/6/2019.
 */
@Component
public class AltFacilityIdentifierItemReader extends FrsHibernateCursorItemReader<AlternativeFacilityIdCaerFrsView> {

    @Autowired
    public AltFacilityIdentifierItemReader(SessionFactory sessionFactory) {
        super(sessionFactory, AlternativeFacilityIdCaerFrsView.class);
    }
}
