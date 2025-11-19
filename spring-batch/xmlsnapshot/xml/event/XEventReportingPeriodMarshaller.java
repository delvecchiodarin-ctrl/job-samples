package gov.epa.eis.batch.xmlsnapshot.xml.event;

import gov.epa.eis.batch.cers.XEventLocation;
import gov.epa.eis.batch.cers.XEventReportingPeriod;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.EventReportingPeriod;

import java.util.List;

/**
 * XEventReportingPeriodMarshaller
 */
public class XEventReportingPeriodMarshaller implements XMarshaller<XEventReportingPeriod, EventReportingPeriod> {
    private XEventLocationMarshaller xEventLocationMarshaller;

    public XEventReportingPeriodMarshaller() {
        this.xEventLocationMarshaller = new XEventLocationMarshaller();
    }

    public XEventReportingPeriod marshall(final EventReportingPeriod eventReportingPeriod) {
        XEventReportingPeriod xEventReportingPeriod = new XEventReportingPeriod();
        xEventReportingPeriod.setEventStageCode(XMarshallerUtility.getCode(eventReportingPeriod.getEventStageCode()));
        xEventReportingPeriod.setEventBeginDate(eventReportingPeriod.getEventBeginDate() != null ? eventReportingPeriod.getEventBeginDate().getXmlDate() : null);
        xEventReportingPeriod.setEventEndDate(eventReportingPeriod.getEventEndDate() != null ? eventReportingPeriod.getEventEndDate().getXmlDate() : null);
        xEventReportingPeriod.setBeginHour(eventReportingPeriod.getBeginHour() != null ? eventReportingPeriod.getBeginHour().toString() : null);
        xEventReportingPeriod.setEndHour(eventReportingPeriod.getEndHour() != null ? eventReportingPeriod.getEndHour().toString() : null);
        xEventReportingPeriod.setEventReportingPeriodComment(eventReportingPeriod.getEventReportingPeriodComment());
        List<XEventLocation> xEventLocations = xEventReportingPeriod.getEventLocations();
        eventReportingPeriod.getEventLocations().stream()
                .forEach(l -> xEventLocations.add(xEventLocationMarshaller.marshall(l)));

        return xEventReportingPeriod;
    }
}