package gov.epa.eis.batch.xmlsnapshot.xml;

import gov.epa.eis.batch.cers.XFacilityIdentification;
import gov.epa.eis.model.AlternativeFacilityIdentification;
import gov.epa.eis.model.County;
import gov.epa.eis.model.FacilitySite;
import gov.epa.eis.model.Location;
import gov.epa.eis.model.ProgramSystemCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * XFacilitySiteIdentifiersMarshaller
 */
public class XFacilitySiteIdentifiersMarshaller implements XMarshaller<List<XFacilityIdentification>, Collection<AlternativeFacilityIdentification>> {
    private String programSystemCode;
    private FacilitySite facilitySite;

    public XFacilitySiteIdentifiersMarshaller(String programSystemCode) {
        this.programSystemCode = programSystemCode;
    }

    public void setFacilitySite(final FacilitySite facilitySite) {
        this.facilitySite = facilitySite;
    }

    public List<XFacilityIdentification> marshall(final String eisId, final Collection<AlternativeFacilityIdentification> alternateIdentifiers) {
        //define identifiers, Always include the EIS id
        // and any PSC specific active Id (returned as part of model alt ids collection).
        List<XFacilityIdentification> xIdentifications = new ArrayList<>();
        //Add EIS Identifier
        xIdentifications.add(marshall(eisId));
        //Add Alternate Identifier based on PSC ownership
        xIdentifications.addAll(marshall(alternateIdentifiers));
        return xIdentifications;
    }

    public List<XFacilityIdentification> marshall(final Collection<AlternativeFacilityIdentification> alternateIdentifiers) {
        List<XFacilityIdentification> xIdentifications = new ArrayList<>();
        //Add PSC specific Identifier (if not EPA agency-owned data set), also make sure it's not expired
        alternateIdentifiers.stream()
                .filter(ai -> ai.getProgramSystemCode().getCode().equals(this.programSystemCode) && ai.isNonExpired())
                .forEach(ai -> xIdentifications.add(marshall(ai)));

        return xIdentifications;
    }

    private XFacilityIdentification marshall(final String eisId) {
        XFacilityIdentification xIdentification = new XFacilityIdentification();
        xIdentification.setFacilitySiteIdentifier(eisId);
        xIdentification.setProgramSystemCode(ProgramSystemCode.EIS_CODE);
        addLocationToIdentifier(xIdentification);
        return xIdentification;
    }

    private XFacilityIdentification marshall(final AlternativeFacilityIdentification identifier) {
        XFacilityIdentification xIdentification = new XFacilityIdentification();
        xIdentification.setFacilitySiteIdentifier(identifier.getAlternateIdentifier());
        xIdentification.setProgramSystemCode(identifier.getProgramSystemCode().getCode());
        addLocationToIdentifier(xIdentification);
        xIdentification.setEffectiveDate(identifier.getEffectiveDate() != null ? identifier.getEffectiveDate().getXmlDate() : null);
        xIdentification.setEndDate(identifier.getEndDate() != null ? identifier.getEndDate().getXmlDate() : null);
        return xIdentification;
    }

    private void addLocationToIdentifier(final XFacilityIdentification xFacilityIdentification) {
        Location location = facilitySite.getLocation();
        County county = location.getCounty();
        if (county != null) {
            xFacilityIdentification.setStateAndCountyFIPSCode(county.getStateCode().getCode() + county.getCountyCode());
        }

        if (location.getTribalCode() != null) {
            xFacilityIdentification.setTribalCode(location.getTribalCode().getCode());
        }
    }
}