package gov.epa.eis.batch.xmlsnapshot.xml.event;

import gov.epa.eis.batch.cers.XEvent;
import gov.epa.eis.batch.cers.XEventReportingPeriod;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.Event;

import java.util.List;

/**
 * XEventMarshaller
 */
public class XEventMarshaller implements XMarshaller<XEvent, Event> {
    private XEventReportingPeriodMarshaller xEventReportingPeriodMarshaller
            = new XEventReportingPeriodMarshaller();

    public XEventMarshaller() {
        this.xEventReportingPeriodMarshaller = new XEventReportingPeriodMarshaller();
    }

    public XEvent marshall(final Event event) {
        XEvent xEvent = new XEvent();
        xEvent.setEventIdentifier(event.getEventIdentifier());
        xEvent.setProgramSystemCode(event.getProgramSystemCode() != null ? event.getProgramSystemCode().getCode() : null);
        xEvent.setEventName(event.getEventName());
        xEvent.setEventSizeSourceCode(XMarshallerUtility.getCode(event.getEventSizeSourceCode()));
        xEvent.setEventClassificationCode(XMarshallerUtility.getCode(event.getEventClassificationCode()));
        xEvent.setContainmentDate(event.getContainmentDate() != null ? event.getContainmentDate().getXmlDate() : null);
        xEvent.setFuelConsumptionAndEmissionsModelCode(XMarshallerUtility.getCode(event.getFuelConsumptionAndEmissionsModelCode()));
        xEvent.setFuelSelectionCode(XMarshallerUtility.getCode(event.getFuelSelectionCode()));
        xEvent.setFuelTypeModelCode(XMarshallerUtility.getCode(event.getFuelTypeModelCode()));

        xEvent.setGroundBasedDataSourceCode(XMarshallerUtility.getCode(event.getGroundBasedDataSourceCode()));
        xEvent.setIgnitionLocationCode(XMarshallerUtility.getCode(event.getIgnitionLocationCode()));
        xEvent.setIgnitionMethodCode(XMarshallerUtility.getCode(event.getIgnitionMethodCode()));
        xEvent.setIgnitionOrientationCode(XMarshallerUtility.getCode(event.getIgnitionOrientationCode()));
        xEvent.setLandManager(event.getLandManager());
        xEvent.setLocationDescription(event.getLocationDescription());
        xEvent.setRecurrenceIndicatorCode(event.getRecurrenceIndicatorCode() ? "Y" : "N");
        xEvent.setRecurrenceYear(event.getRecurrenceYear() != null ? event.getRecurrenceYear().getValue() : null);
        xEvent.setRemoteSensingDataSourceCode(XMarshallerUtility.getCode(event.getRemoteSensingDataSourceCode()));
        xEvent.setEventComment(event.getEventComment());

        //Add reporting periods
        List<XEventReportingPeriod> xEventReportingPeriods = xEvent.getEventReportingPeriods();
        event.getReportingPeriods().stream()
                .forEach(rp -> xEventReportingPeriods.add(xEventReportingPeriodMarshaller.marshall(rp)));

        return xEvent;
    }
}