package gov.epa.eis.batch.xmlsnapshot.xml.event;

import gov.epa.eis.batch.cers.XEventEmissionsProcess;
import gov.epa.eis.batch.cers.XEventLocation;
import gov.epa.eis.batch.xmlsnapshot.xml.XGeographicCoordinateMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.EventLocation;

import java.util.List;

/**
 * XEventLocationMarshaller
 */
public class XEventLocationMarshaller implements XMarshaller<XEventLocation, EventLocation> {
    private XEventEmissionProcessMarshaller xEventEmissionProcessMarshaller;
    private XGeographicCoordinateMarshaller xGeographicCoordinateMarshaller;

    public XEventLocationMarshaller() {
        this.xEventEmissionProcessMarshaller = new XEventEmissionProcessMarshaller();
        this.xGeographicCoordinateMarshaller = new XGeographicCoordinateMarshaller();
    }

    public XEventLocation marshall(final EventLocation eventLocation) {
        XEventLocation xEventLocation = new XEventLocation();
        xEventLocation.setStateAndCountyFIPSCode(eventLocation.getCounty() != null ? eventLocation.getCounty().getCode() : null);
//        xEventLocation.setStateAndCountryFIPSCode(eventLocation.getCounty() != null ? eventLocation.getCounty().getStateCode().getCode() : null);
        xEventLocation.setTribalCode(eventLocation.getTribalCode() != null ? eventLocation.getTribalCode().getCode() : null);

        xEventLocation.setEventGeographicCoordinates(
                eventLocation.getEventGeographicCoordinates() != null
                        ? xGeographicCoordinateMarshaller.marshall(eventLocation.getEventGeographicCoordinates())
                        : null);

        List<XEventEmissionsProcess> xEventEmissionsProcesses = xEventLocation.getEventEmissionsProcess();
        eventLocation.getEventProcesses().stream()
                .forEach(p -> xEventEmissionsProcesses.add(xEventEmissionProcessMarshaller.marshall(p)));

        return xEventLocation;
    }
}