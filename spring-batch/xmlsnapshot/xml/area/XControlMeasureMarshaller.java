package gov.epa.eis.batch.xmlsnapshot.xml.area;

import gov.epa.eis.batch.cers.XControlMeasure;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.ControlMeasure;

/**
 * XControlMeasureMarshaller
 */
public class XControlMeasureMarshaller implements XMarshaller<XControlMeasure, ControlMeasure> {
    public XControlMeasure marshall(final ControlMeasure controlMeasure) {
        XControlMeasure xControlMeasure = new XControlMeasure();
        xControlMeasure.setControlMeasureCode(XMarshallerUtility.getCode(controlMeasure.getControlMeasureCode()));
        return xControlMeasure;
    }
}