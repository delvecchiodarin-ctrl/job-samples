package gov.epa.eis.batch.xmlsnapshot.processor;

import gov.epa.eis.batch.cers.XFacilitySite;
import gov.epa.eis.batch.xmlsnapshot.xml.facility.XFacilitySiteMarshaller;
import gov.epa.eis.model.FacilitySite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemProcessor;

public class FacilitySnapshotItemProcessor implements ItemProcessor<FacilitySite,XFacilitySite> {

    private static final Log LOG = LogFactory.getLog(FacilitySnapshotItemProcessor.class);
    private String programSystemCode;

    @Override
    public XFacilitySite process(FacilitySite facilitySite) throws Exception {

        LOG.info("Process FacilitySite facilitySiteId=" + facilitySite.getId());

        // Build your xml object here. This method should return a xml object that can be passed to
        XFacilitySiteMarshaller xFacilitySiteMarshaller = new XFacilitySiteMarshaller(programSystemCode);
        return xFacilitySiteMarshaller.marshall(facilitySite);
    }

    public void setProgramSystemCode(String programSystemCode) {
        this.programSystemCode = programSystemCode;
        LOG.info("programSystemCode=" + programSystemCode);
    }
}