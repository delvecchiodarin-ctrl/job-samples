package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XAlternativeFacilityName;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.AlternativeFacilityName;
import org.apache.commons.lang3.StringUtils;

/**
 * XAlternativeFacilityNameMarshaller
 */
public class XAlternativeFacilityNameMarshaller implements XMarshaller<XAlternativeFacilityName, AlternativeFacilityName> {
    public XAlternativeFacilityName marshall(final AlternativeFacilityName alternativeFacilityName) {
        XAlternativeFacilityName xAlternativeFacilityName = new XAlternativeFacilityName();
        xAlternativeFacilityName.setAlternativeName(alternativeFacilityName.getAlternativeName());
        xAlternativeFacilityName.setProgramSystemCode(alternativeFacilityName.getProgramSystemCode().getCode());
        xAlternativeFacilityName.setAlternativeNameTypeText(StringUtils.isNotBlank(alternativeFacilityName.getAlternativeNameType()) ? alternativeFacilityName.getAlternativeNameType() : null);
        xAlternativeFacilityName.setEffectiveDate(alternativeFacilityName.getEffectiveDate() != null ? alternativeFacilityName.getEffectiveDate().getXmlDate() : null);
        return xAlternativeFacilityName;
    }
}