package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XFacilityNAICS;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.FacilityNaics;

/**
 * XFacilityNaicsMarshaller
 */
public class XFacilityNaicsMarshaller implements XMarshaller<XFacilityNAICS, FacilityNaics> {
    public XFacilityNAICS marshall(final FacilityNaics facilityNaics) {
        XFacilityNAICS xFacilityNAICS = new XFacilityNAICS();
        xFacilityNAICS.setNAICSCode(facilityNaics.getNaicsCode().getCode().toString());
        xFacilityNAICS.setNAICSType(facilityNaics.getFacilityNaicsType());
        return xFacilityNAICS;
    }
}