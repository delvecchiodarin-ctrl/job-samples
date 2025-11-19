package gov.epa.eis.batch.xmlsnapshot.xml.area;

import gov.epa.eis.batch.cers.XControlPollutantDataType;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.ControlPollutant;

/**
 * XControlPollutantMarshaller
 */
public class XControlPollutantMarshaller implements XMarshaller<XControlPollutantDataType, ControlPollutant> {
    public XControlPollutantDataType marshall(final ControlPollutant controlPollutant) {
        XControlPollutantDataType xControlPollutant = new XControlPollutantDataType();
        xControlPollutant.setPollutantCode(XMarshallerUtility.getCode(controlPollutant.getPollutantCode()));
        xControlPollutant.setPercentControlMeasuresReductionEfficiency(controlPollutant.getPercentMeasureReductionEfficiency() != null ? controlPollutant.getPercentMeasureReductionEfficiency().getValue() : null);
        return xControlPollutant;
    }
}