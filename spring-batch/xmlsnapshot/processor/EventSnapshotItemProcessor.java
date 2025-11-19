package gov.epa.eis.batch.xmlsnapshot.processor;

import gov.epa.eis.batch.cers.XEvent;
import gov.epa.eis.batch.xmlsnapshot.xml.event.XEventMarshaller;
import org.springframework.batch.item.ItemProcessor;

import gov.epa.eis.model.Event;

public class EventSnapshotItemProcessor implements ItemProcessor<Event,XEvent> {

    @Override
    public XEvent process(Event event) throws Exception {
        // Build your xml object here. This method should return a xml object that can be passed to
        XEventMarshaller xEventMarshaller = new XEventMarshaller();
        return xEventMarshaller.marshall(event);
    }

}