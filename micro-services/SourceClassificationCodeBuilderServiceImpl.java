package gov.epa.eis.batch.builder.impl;

import gov.epa.eis.batch.builder.SourceClassificationCodeBuilderService;
import gov.epa.eis.service.SourceClassificationCodePullService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

/**
 * SourceClassificationCodeBuilderServiceImpl is a Spring bean singleton that can be invoked by a job scheduler.
 */
public final class SourceClassificationCodeBuilderServiceImpl implements SourceClassificationCodeBuilderService {
    private static final Log LOG = LogFactory.getLog(SourceClassificationCodeBuilderServiceImpl.class);

    private SourceClassificationCodePullService sourceClassificationCodePullService;

    @Override
    public void pullSourceClassificationCodes() {
        //pull SCCs from Synaptica as of yesterday...
        Date lastPullDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(lastPullDate);
        lastPullDate.setTime( c.getTime().getTime() );

        sourceClassificationCodePullService.updateSourceClassificationCodes(lastPullDate);
    }

    @Autowired
    public void setSourceClassificationCodePullService(final SourceClassificationCodePullService sourceClassificationCodePullService) {
        this.sourceClassificationCodePullService = sourceClassificationCodePullService;
    }
}
