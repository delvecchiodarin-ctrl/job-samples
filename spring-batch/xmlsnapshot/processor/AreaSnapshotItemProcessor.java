package gov.epa.eis.batch.xmlsnapshot.processor;

import gov.epa.eis.batch.cers.XLocation;
import gov.epa.eis.batch.xmlsnapshot.xml.area.XLocationMarshaller;
import gov.epa.eis.model.dto.LocationDto;
import org.springframework.batch.item.ItemProcessor;

public class AreaSnapshotItemProcessor implements ItemProcessor<LocationDto,XLocation> {

    @Override
    public XLocation process(LocationDto locationDto) throws Exception {
        // Build your xml object here. This method should return a xml object that can be passed to
        XLocationMarshaller xLocationMarshaller = new XLocationMarshaller();
        return xLocationMarshaller.marshall(locationDto);
    }

}