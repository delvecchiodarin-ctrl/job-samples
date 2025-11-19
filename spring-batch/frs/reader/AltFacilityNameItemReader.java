package gov.epa.eis.batch.frs.reader;

import gov.epa.eis.model.view.AlternativeFacilityNameCaerFrsView;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/6/2019.
 */
@Component
public class AltFacilityNameItemReader extends FrsHibernateCursorItemReader<AlternativeFacilityNameCaerFrsView> {

    @Autowired
    public AltFacilityNameItemReader(SessionFactory sessionFactory) {
        super(sessionFactory, AlternativeFacilityNameCaerFrsView.class);
    }
}