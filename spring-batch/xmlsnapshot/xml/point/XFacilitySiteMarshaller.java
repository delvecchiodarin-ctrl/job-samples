package gov.epa.eis.batch.xmlsnapshot.xml.point;

import gov.epa.eis.batch.cers.XEmissionsUnit;
import gov.epa.eis.batch.cers.XFacilityIdentification;
import gov.epa.eis.batch.cers.XFacilitySite;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.AlternativeFacilityIdentification;
import gov.epa.eis.model.County;
import gov.epa.eis.model.FacilitySite;
import gov.epa.eis.model.Location;
import gov.epa.eis.model.ProgramSystemCode;

import java.util.List;

/**
 * XFacilitySiteMarshaller
 */
public class XFacilitySiteMarshaller implements XMarshaller<XFacilitySite, FacilitySite> {
    private String programSystemCode;
    private XFacilitySiteIdentifiersMarshaller xFacilitySiteIdentifiersMarshaller;
    private XEmissionUnitMarshaller xEmissionUnitMarshaller;

    public XFacilitySiteMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteIdentifiersMarshaller = new XFacilitySiteIdentifiersMarshaller(this.programSystemCode);
        this.xEmissionUnitMarshaller = new XEmissionUnitMarshaller(this.programSystemCode);
    }

    public XFacilitySite marshall(final FacilitySite facilitySite) {
        if (facilitySite.getEisFacilitySiteId() == null)
            return null;

        XFacilitySite xFacilitySite = new XFacilitySite();

        //Add EIS and Alternate Identifiers
        xFacilitySiteIdentifiersMarshaller.setFacilitySite(facilitySite);
        xFacilitySite.getFacilityIdentifications().addAll(
                xFacilitySiteIdentifiersMarshaller.marshall(facilitySite.getEisFacilitySiteId().toString(), facilitySite.getAlternateIdentifiers())
        );

        //Add emission units
        List<XEmissionsUnit> xEmissionsUnits = xFacilitySite.getEmissionsUnits();
        facilitySite.getUnits().stream().forEach(eu -> xEmissionsUnits.add(xEmissionUnitMarshaller.marshall(eu)));

        return xFacilitySite;
    }
}