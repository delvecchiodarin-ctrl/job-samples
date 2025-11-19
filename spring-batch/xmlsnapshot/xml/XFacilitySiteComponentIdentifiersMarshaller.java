package gov.epa.eis.batch.xmlsnapshot.xml;

import gov.epa.eis.batch.cers.XIdentificationDataType;
import gov.epa.eis.model.AlternateIdentifier;
import gov.epa.eis.model.ProgramSystemCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * XFacilitySiteComponentIdentifiersMarshaller
 */
public class XFacilitySiteComponentIdentifiersMarshaller implements XMarshaller<List<XIdentificationDataType>, Collection<AlternateIdentifier>> {
    private String programSystemCode;

    public XFacilitySiteComponentIdentifiersMarshaller(String programSystemCode) {
        this.programSystemCode = programSystemCode;
    }

    public List<XIdentificationDataType> marshall(final String eisId, final Collection<AlternateIdentifier> alternateIdentifiers) {
        //define identifiers, Always include the EIS id
        // and any PSC specific active Id (returned as part of model alt ids collection).
        List<XIdentificationDataType> xIdentifications = new ArrayList<>();
        //Add EIS Identifier
        xIdentifications.add(marshall(eisId));
        //Add Alternate Identifier based on PSC ownership
        xIdentifications.addAll(marshall(alternateIdentifiers));
        return xIdentifications;
    }

    public List<XIdentificationDataType> marshall(final Collection<AlternateIdentifier> alternateIdentifiers) {
        List<XIdentificationDataType> xIdentifications = new ArrayList<>();
        //Add PSC specific Identifier (if not EPA agency-owned data set), also make sure it's not expired
        alternateIdentifiers.stream()
                .filter(ai -> ai.getProgramSystemCode().getCode().equals(this.programSystemCode) && ai.isNonExpired())
                .forEach(ai -> xIdentifications.add(marshall(ai)));

        return xIdentifications;
    }

    private XIdentificationDataType marshall(final String eisId) {
        XIdentificationDataType xIdentification = new XIdentificationDataType();
        xIdentification.setIdentifier(eisId);
        xIdentification.setProgramSystemCode(ProgramSystemCode.EIS_CODE);
        return xIdentification;
    }

    private XIdentificationDataType marshall(final AlternateIdentifier identifier) {
        XIdentificationDataType xIdentification = new XIdentificationDataType();
        xIdentification.setIdentifier(identifier.getAlternateIdentifier());
        xIdentification.setProgramSystemCode(identifier.getProgramSystemCode().getCode());
        xIdentification.setEffectiveDate(identifier.getEffectiveDate() != null ? identifier.getEffectiveDate().getXmlDate() : null);
        xIdentification.setEndDate(identifier.getEndDate() != null ? identifier.getEndDate().getXmlDate() : null);
        return xIdentification;
    }
}