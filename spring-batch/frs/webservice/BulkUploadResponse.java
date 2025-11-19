package gov.epa.eis.batch.frs.webservice;

import java.io.Serializable;

/**
 * Created by DDelVecc on 5/8/2019.
 */
public class BulkUploadResponse extends FrsResponse {
    private String transactionId;

    public String getTransactionId() {
        return transactionId;
    }
}
