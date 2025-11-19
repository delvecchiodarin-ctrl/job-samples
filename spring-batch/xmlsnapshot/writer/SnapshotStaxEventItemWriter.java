package gov.epa.eis.batch.xmlsnapshot.writer;

import gov.epa.eis.batch.cers.XCERS;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.Marshaller;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DDelVecc on 6/27/2018.
 */
public class SnapshotStaxEventItemWriter<T> extends StaxEventItemWriter<T> {

    public SnapshotStaxEventItemWriter(final Class<T> xClass, final CERSXmlHeaderCallback cersXmlHeaderCallback, final String reportRequestOutputFilePath) {
        Map<String, String> rootElementAttributes = new HashMap<>();
        rootElementAttributes.put("xmlns:cer", "http://www.exchangenetwork.net/schema/cer/2");
        rootElementAttributes.put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootElementAttributes.put("xsi:schemaLocation", "http://www.exchangenetwork.net/schema/cer/2 http://www.exchangenetwork.net/schema/cer/2/index.xsd");
        super.setRootTagName("cer:CERS");
        super.setRootElementAttributes(rootElementAttributes);

        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(XCERS.class, xClass);
//        jaxb2Marshaller.setClassesToBeBound(XCERS.class, XEvent.class, XAttachedFileDataType.class,
//                XMergedEvents.class, XEventReportingPeriod.class, XEventLocation.class,
//                XGeographicCoordinatesDataType.class, XGeospatialParameters.class, XEventEmissionsProcess.class,
//                XEmissionsDataType.class, XCO2Equivalent.class);
        Map<String, Object> marshallerProperties = new HashMap<>();
        marshallerProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        jaxb2Marshaller.setMarshallerProperties(marshallerProperties);
        super.setMarshaller(jaxb2Marshaller);
        super.setForceSync(true);
        super.setTransactional(false);

        super.setHeaderCallback(cersXmlHeaderCallback);

        FileSystemResource fileSystemResource = new FileSystemResource(reportRequestOutputFilePath);

        super.setResource(fileSystemResource);
    }

}
