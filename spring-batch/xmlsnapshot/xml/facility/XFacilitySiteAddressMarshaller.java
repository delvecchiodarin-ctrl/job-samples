package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XFacilitySiteAddress;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.FacilitySite;
import org.apache.commons.lang3.StringUtils;

/**
 * XFacilitySiteAddressMarshaller
 */
public class XFacilitySiteAddressMarshaller implements XMarshaller<XFacilitySiteAddress, FacilitySite> {
    public XFacilitySiteAddress marshall(final FacilitySite facilitySite) {
        XFacilitySiteAddress xFacilitySiteAddress = new XFacilitySiteAddress();
        xFacilitySiteAddress.setLocationAddressText(StringUtils.isNotBlank(facilitySite.getMailingAddress()) ? facilitySite.getMailingAddress() : null);
        xFacilitySiteAddress.setSupplementalLocationText(StringUtils.isNotBlank(facilitySite.getSupplementalMailingAddress()) ? facilitySite.getSupplementalMailingAddress() : null);
        xFacilitySiteAddress.setLocalityName(StringUtils.isNotBlank(facilitySite.getLocality()) ? facilitySite.getLocality() : null);
        xFacilitySiteAddress.setLocationAddressStateCode(facilitySite.getStateCode() != null ? facilitySite.getStateCode().getStateUspsCode() : null);
        xFacilitySiteAddress.setLocationAddressPostalCode(StringUtils.isNotBlank(facilitySite.getPostalCode()) ? facilitySite.getPostalCode() : null);
        xFacilitySiteAddress.setLocationAddressCountryCode(XMarshallerUtility.getCode(facilitySite.getCountryCode()));
        xFacilitySiteAddress.setAddressComment(StringUtils.isNotBlank(facilitySite.getAddressComment()) ? facilitySite.getAddressComment() : null);
        return xFacilitySiteAddress;
    }
}