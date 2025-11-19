package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XAffiliationOrganization;
import gov.epa.eis.batch.cers.XFacilitySiteAffiliation;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.FacilitySite;
import org.apache.commons.lang3.StringUtils;

/**
 * XFacilitySiteAffiliationMarshaller
 */
public class XFacilitySiteAffiliationMarshaller implements XMarshaller<XFacilitySiteAffiliation, FacilitySite> {
    public XFacilitySiteAffiliation marshall(final FacilitySite facilitySite) {
        XFacilitySiteAffiliation xAffiliationDataType = null;
        if (StringUtils.isNotBlank(facilitySite.getCompanyName())) {
            xAffiliationDataType = new XFacilitySiteAffiliation();
            xAffiliationDataType.setAffiliationTypeCode(XMarshallerUtility.getCode(facilitySite.getAffiliationTypeCode()));
            XAffiliationOrganization xAffiliationOrganization = new XAffiliationOrganization();
            xAffiliationOrganization.setOrganizationFormalName(facilitySite.getCompanyName());
            xAffiliationDataType.getAffiliationOrganizations().add(xAffiliationOrganization);
        }

        return xAffiliationDataType;
    }
}