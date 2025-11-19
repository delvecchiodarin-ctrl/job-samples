package gov.epa.eis.batch.frs.reader;

import gov.epa.eis.model.view.FacilitySiteCaerFrsView;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/6/2019.
 */
@Component
public class FacilityItemReader extends FrsHibernateCursorItemReader<FacilitySiteCaerFrsView> {

    @Autowired
    public FacilityItemReader(SessionFactory sessionFactory) {
        super(sessionFactory, FacilitySiteCaerFrsView.class);
    }
}