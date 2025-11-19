package gov.epa.eis.batch.xmlsnapshot.processor;

import gov.epa.eis.batch.cers.XFacilitySite;
import gov.epa.eis.batch.xmlsnapshot.xml.point.XFacilitySiteMarshaller;
import gov.epa.eis.model.FacilitySite;
import org.springframework.batch.item.ItemProcessor;

public class PointEmissionSnapshotItemProcessor implements ItemProcessor<FacilitySite,XFacilitySite> {

    private String programSystemCode;

    @Override
    public XFacilitySite process(FacilitySite facilitySite) throws Exception {
        // Build your xml object here. This method should return a xml object that can be passed to
        XFacilitySiteMarshaller xFacilitySiteMarshaller = new XFacilitySiteMarshaller(programSystemCode);
        return xFacilitySiteMarshaller.marshall(facilitySite);
    }

    public void setProgramSystemCode(String programSystemCode) {
        this.programSystemCode = programSystemCode;
    }
}