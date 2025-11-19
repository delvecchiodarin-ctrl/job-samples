package gov.epa.eis.batch.xmlsnapshot.reader;

import org.springframework.batch.item.database.HibernateCursorItemReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DDelVecc on 6/27/2018.
 */
public class SnapshotHibernateCursorItemReader<T> extends HibernateCursorItemReader<T> {

    public SnapshotHibernateCursorItemReader(final Long reportRequestId, final String hqlQueryString) {
        super.setUseStatelessSession(false);
        super.setQueryString(hqlQueryString);

        //set report request id to target...
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("reportRequestId", reportRequestId);
        super.setParameterValues(parameterValues);
    }

    public SnapshotHibernateCursorItemReader(final Long reportRequestId, final Long dataSetId, final String hqlQueryString) {
        super.setUseStatelessSession(false);
        super.setQueryString(hqlQueryString);

        //set report request id to target...
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("reportRequestId", reportRequestId);
        parameterValues.put("dataSetId", dataSetId);
        super.setParameterValues(parameterValues);
    }
}
