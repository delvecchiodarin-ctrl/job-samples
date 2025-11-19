package gov.epa.eis.batch.frs.webservice;

import java.io.Serializable;

/**
 * Created by DDelVecc on 5/8/2019.
 */
public abstract class FrsResponse implements Serializable {
    private FrsRequestResponsePayload frsRequestResponsePayload = null;

    public FrsRequestResponsePayload getFrsRequestResponsePayload() {
        return frsRequestResponsePayload;
    }

    public void setFrsRequestResponsePayload(final FrsRequestResponsePayload frsRequestResponsePayload) {
        this.frsRequestResponsePayload = frsRequestResponsePayload;
    }
}
