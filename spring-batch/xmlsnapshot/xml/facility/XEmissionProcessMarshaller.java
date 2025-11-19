package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XProcessDataType;
import gov.epa.eis.batch.cers.XRegulationDataType;
import gov.epa.eis.batch.cers.XReleasePointApportionment;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteComponentIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.EmissionsProcess;
import gov.epa.eis.model.Regulation;
import gov.epa.eis.model.ReleasePointApportionment;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * XEmissionProcessMarshaller
 */
public class XEmissionProcessMarshaller implements XMarshaller<XProcessDataType, EmissionsProcess> {
    private String programSystemCode;
    private XFacilitySiteComponentIdentifiersMarshaller xFacilitySiteComponentIdentifiersMarshaller;
    private XRegulationMarshaller xRegulationMarshaller;
    private XReleasePointApportionmentMarshaller xReleasePointApportionmentMarshaller;

    public XEmissionProcessMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteComponentIdentifiersMarshaller
                = new XFacilitySiteComponentIdentifiersMarshaller(this.programSystemCode);
        this.xRegulationMarshaller = new XRegulationMarshaller();
        this.xReleasePointApportionmentMarshaller = new XReleasePointApportionmentMarshaller(this.programSystemCode);
    }

    public XProcessDataType marshall(final EmissionsProcess emissionProcess) {
        if (emissionProcess.getEisEmissionsProcessId() == null)
            return null;

        XProcessDataType xProcessDataType = new XProcessDataType();
        xProcessDataType.setSourceClassificationCode(XMarshallerUtility.getCode(emissionProcess.getSourceClassificationCode()));
        xProcessDataType.setEmissionsTypeCode(XMarshallerUtility.getCode(emissionProcess.getEmissionsTypeCode()));
        xProcessDataType.setAircraftEngineTypeCode(XMarshallerUtility.getCode(emissionProcess.getAircraftEngineTypeCode()));
        // xProcessDataType.setLastEmissionsYear(emissionProcess.getLastInventoryYear() != null ? emissionProcess.getLastInventoryYear().getValue() : null);
        xProcessDataType.setProcessComment(StringUtils.isNotBlank(emissionProcess.getEisComment()) ? (emissionProcess.getEisComment()) : null);
        xProcessDataType.setProcessDescription(StringUtils.isNotBlank(emissionProcess.getDescription()) ? (emissionProcess.getDescription()) : null);
        xProcessDataType.setProcessStatusCode(XMarshallerUtility.getCode(emissionProcess.getOperatingStatusCode()));
        xProcessDataType.setProcessStatusCodeYear(emissionProcess.getOperatingStatusYear() != null ? emissionProcess.getOperatingStatusYear().getValue() : null);

        //Add EIS and Alternate Identifiers
        xProcessDataType.getProcessIdentifications().addAll(
                xFacilitySiteComponentIdentifiersMarshaller.marshall(emissionProcess.getEisEmissionsProcessId().toString(), emissionProcess.getAlternateIdentifiers())
        );

        //Add regulations
        List<XRegulationDataType> xRegulationDataTypes = xProcessDataType.getProcessRegulations();
        emissionProcess.getRegulations().stream()
                .forEach(r -> xRegulationDataTypes.add(xRegulationMarshaller.marshall(r)));

        //Add apportionments
        List<XReleasePointApportionment> xReleasePointApportionments = xProcessDataType.getReleasePointApportionments();
        emissionProcess.getApportionment().stream()
                .forEach(ap -> xReleasePointApportionments.add(xReleasePointApportionmentMarshaller.marshall(ap)));

        return xProcessDataType;
    }
}