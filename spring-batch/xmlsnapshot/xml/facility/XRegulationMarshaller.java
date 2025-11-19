package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XRegulationDataType;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.Regulation;
import org.apache.commons.lang3.StringUtils;

/**
 * XRegulationMarshaller
 */
public class XRegulationMarshaller implements XMarshaller<XRegulationDataType, Regulation> {
    public XRegulationDataType marshall(final Regulation regulation) {
        XRegulationDataType xRegulationDataType = new XRegulationDataType();
        xRegulationDataType.setRegulatoryCode(XMarshallerUtility.getCode(regulation.getRegulatoryCode()));
        xRegulationDataType.setAgencyCodeText(StringUtils.isNotBlank(regulation.getAgencyCode()) ? regulation.getAgencyCode() : null);
        xRegulationDataType.setRegulatoryStartYear(regulation.getRegulatoryYear() != null ? regulation.getRegulatoryYear().getValue() : null);
        xRegulationDataType.setRegulatoryEndYear(regulation.getRegulatoryEndYear() != null ? regulation.getRegulatoryEndYear().getValue() : null);
        xRegulationDataType.setRegulationComment(StringUtils.isNotBlank(regulation.getComment()) ? (regulation.getComment()) : null);
        return xRegulationDataType;
    }
}