package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XControlPollutantDataType;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.SiteControlPollutant;

/**
 * XControlPollutantMarshaller
 */
public class XControlPollutantMarshaller implements XMarshaller<XControlPollutantDataType, SiteControlPollutant> {
    public XControlPollutantDataType marshall(final SiteControlPollutant controlPollutant) {
        XControlPollutantDataType xControlPollutant = new XControlPollutantDataType();
        xControlPollutant.setPollutantCode(XMarshallerUtility.getCode(controlPollutant.getPollutantCode()));
        xControlPollutant.setPercentControlMeasuresReductionEfficiency(controlPollutant.getReductionEfficiencyPct() != null ? controlPollutant.getReductionEfficiencyPct().getValue() : null);
        return xControlPollutant;
    }
}