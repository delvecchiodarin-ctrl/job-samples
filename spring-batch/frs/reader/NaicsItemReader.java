package gov.epa.eis.batch.frs.reader;

import gov.epa.eis.model.view.NaicsCareFrsView;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by DDelVecc on 4/6/2019.
 */
@Component
public class NaicsItemReader extends FrsHibernateCursorItemReader<NaicsCareFrsView> {

    @Autowired
    public NaicsItemReader(SessionFactory sessionFactory) {
        super(sessionFactory, NaicsCareFrsView.class);
    }
}