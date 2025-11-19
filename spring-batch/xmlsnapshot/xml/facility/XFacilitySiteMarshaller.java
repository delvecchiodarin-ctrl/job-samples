package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XAlternativeFacilityName;
import gov.epa.eis.batch.cers.XEmissionsUnit;
import gov.epa.eis.batch.cers.XFacilityIdentification;
import gov.epa.eis.batch.cers.XFacilityNAICS;
import gov.epa.eis.batch.cers.XFacilitySite;
import gov.epa.eis.batch.cers.XFacilitySiteControl;
import gov.epa.eis.batch.cers.XFacilitySitePath;
import gov.epa.eis.batch.cers.XReleasePoint;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XGeographicCoordinateMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.AlternativeFacilityIdentification;
import gov.epa.eis.model.County;
import gov.epa.eis.model.FacilityNaics;
import gov.epa.eis.model.FacilitySite;
import gov.epa.eis.model.Location;
import gov.epa.eis.model.ProgramSystemCode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * XFacilitySiteMarshaller
 */
public class XFacilitySiteMarshaller implements XMarshaller<XFacilitySite, FacilitySite> {
    private String programSystemCode;
    private XFacilitySiteIdentifiersMarshaller xFacilitySiteIdentifiersMarshaller;
    private XFacilityNaicsMarshaller xFacilityNaicsMarshaller;
    private XAlternativeFacilityNameMarshaller xAlternativeFacilityNameMarshaller;
    private XFacilitySiteAddressMarshaller xFacilitySiteAddressMarshaller;
    private XFacilitySiteAffiliationMarshaller xFacilitySiteAffiliationMarshaller;
    private XGeographicCoordinateMarshaller xGeographicCoordinateMarshaller;
    private XEmissionUnitMarshaller xEmissionUnitMarshaller;
    private XSitePathMarshaller xSitePathMarshaller;
    private XSiteControlMarshaller xSiteControlMarshaller;
    private XReleasePointMarshaller xReleasePointMarshaller;

    public XFacilitySiteMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteIdentifiersMarshaller = new XFacilitySiteIdentifiersMarshaller(this.programSystemCode);
        this.xFacilityNaicsMarshaller = new XFacilityNaicsMarshaller();
        this.xAlternativeFacilityNameMarshaller = new XAlternativeFacilityNameMarshaller();
        this.xFacilitySiteAddressMarshaller = new XFacilitySiteAddressMarshaller();
        this.xFacilitySiteAffiliationMarshaller = new XFacilitySiteAffiliationMarshaller();
        this.xGeographicCoordinateMarshaller = new XGeographicCoordinateMarshaller();
        this.xEmissionUnitMarshaller = new XEmissionUnitMarshaller(this.programSystemCode);
        this.xSitePathMarshaller = new XSitePathMarshaller(this.programSystemCode);
        this.xSiteControlMarshaller = new XSiteControlMarshaller(this.programSystemCode);
        this.xReleasePointMarshaller = new XReleasePointMarshaller(this.programSystemCode);
    }

    public XFacilitySite marshall(final FacilitySite facilitySite) {
        if (facilitySite.getEisFacilitySiteId() == null)
            return null;

        XFacilitySite xFacilitySite = new XFacilitySite();

        xFacilitySite.setFacilityCategoryCode(XMarshallerUtility.getCode(facilitySite.getCategoryCode()));
        xFacilitySite.setFacilitySiteName(StringUtils.isNotBlank(facilitySite.getSiteName()) ? facilitySite.getSiteName() : null);
        xFacilitySite.setFacilitySiteDescription(StringUtils.isNotBlank(facilitySite.getSiteDescription()) ? facilitySite.getSiteDescription() : null);
        xFacilitySite.setFacilitySourceTypeCode(XMarshallerUtility.getCode(facilitySite.getSourceTypeCode()));
        xFacilitySite.setFacilitySiteStatusCode(XMarshallerUtility.getCode(facilitySite.getOperatingStatusCode()));
        xFacilitySite.setFacilitySiteStatusCodeYear(facilitySite.getFacilitySiteYear() != null ? facilitySite.getFacilitySiteYear().getValue() : null);
        xFacilitySite.setFacilitySiteComment(StringUtils.isNotBlank(facilitySite.getEisComment()) ? facilitySite.getEisComment() : null);

        //Add facility Naicss
        List<XFacilityNAICS> xFacilityNAICSs = xFacilitySite.getFacilityNAICs();
        if (facilitySite.getNaicsCode() != null) {
            FacilityNaics facilityNaics = new FacilityNaics();
            facilityNaics.setNaicsCode(facilitySite.getNaicsCode());
            facilityNaics.setFacilityNaicsType("PRIMARY");
            xFacilityNAICSs.add(xFacilityNaicsMarshaller.marshall(facilityNaics));
        }
        for (FacilityNaics facilityNaics : facilitySite.getFacilityNaicsCodes()) {
            xFacilityNAICSs.add(xFacilityNaicsMarshaller.marshall(facilityNaics));
        }

        //Add EIS and Alternate Identifiers
        xFacilitySiteIdentifiersMarshaller.setFacilitySite(facilitySite);
        xFacilitySite.getFacilityIdentifications().addAll(
                xFacilitySiteIdentifiersMarshaller.marshall(facilitySite.getEisFacilitySiteId().toString(), facilitySite.getAlternateIdentifiers())
        );

        //Add alternative names (filter by PSC)
        List<XAlternativeFacilityName> xAlternativeFacilityNames = xFacilitySite.getAlternativeFacilityNames();
        facilitySite.getAlternateNames().stream()
                .filter(afn -> afn.getProgramSystemCode().getCode().equals(this.programSystemCode))   //filter by PSC
                .forEach(afn -> {
                    xAlternativeFacilityNames.add(xAlternativeFacilityNameMarshaller.marshall(afn));
                });

        //set address data
        xFacilitySite.getFacilitySiteAddress().add(xFacilitySiteAddressMarshaller.marshall(facilitySite));

        //add geographic coordinate
        xFacilitySite.setFacilitySiteGeographicCoordinates(
                facilitySite.getGeographicCoordinate() != null
                        ? xGeographicCoordinateMarshaller.marshall(facilitySite.getGeographicCoordinate())
                        : null);

        //add FacilitySiteAffiliation
        xFacilitySite.getFacilitySiteAffiliations().add(xFacilitySiteAffiliationMarshaller.marshall(facilitySite));

        //Add emission units
        List<XEmissionsUnit> xEmissionsUnits = xFacilitySite.getEmissionsUnits();
        facilitySite.getUnits().stream().forEach(eu -> xEmissionsUnits.add(xEmissionUnitMarshaller.marshall(eu)));

        //Add release points
        List<XReleasePoint> xReleasePoints = xFacilitySite.getReleasePoints();
        facilitySite.getReleasePoints().stream()
                .forEach(rp -> xReleasePoints.add(xReleasePointMarshaller.marshall(rp)));

        //Add site paths
        List<XFacilitySitePath> xFacilitySitePaths = xFacilitySite.getFacilitySitePaths();
        facilitySite.getSiteControlPaths().stream()
                .forEach(sp -> xFacilitySitePaths.add(xSitePathMarshaller.marshall(sp)));

        //Add site controls
        List<XFacilitySiteControl> xFacilitySiteControls = xFacilitySite.getFacilitySiteControls();
        facilitySite.getSiteControls().stream()
                .forEach(sc -> xFacilitySiteControls.add(xSiteControlMarshaller.marshall(sc)));

        return xFacilitySite;
    }
}