package gov.epa.eis.batch.frs.reader;

import gov.epa.eis.model.view.GeospatialCaerFrsView;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/6/2019.
 */
@Component
public class GeospatialItemReader extends FrsHibernateCursorItemReader<GeospatialCaerFrsView> {

    @Autowired
    public GeospatialItemReader(SessionFactory sessionFactory) {
        super(sessionFactory, GeospatialCaerFrsView.class);
    }
}