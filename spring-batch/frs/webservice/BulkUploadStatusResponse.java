package gov.epa.eis.batch.frs.webservice;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by DDelVecc on 5/8/2019.
 */
public class BulkUploadStatusResponse extends FrsResponse {
    private String transactionId;
    private String status;
    private String transactionFeedback;

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionFeedback() {
        return transactionFeedback;
    }

    public String getStatus() {
        return status;
    }
}
