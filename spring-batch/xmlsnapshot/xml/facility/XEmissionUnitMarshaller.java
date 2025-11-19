package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XEmissionsUnit;
import gov.epa.eis.batch.cers.XProcessDataType;
import gov.epa.eis.batch.cers.XRegulationDataType;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteComponentIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.EmissionsProcess;
import gov.epa.eis.model.EmissionsUnit;
import gov.epa.eis.model.Regulation;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * XEmissionUnitMarshaller
 */
public class XEmissionUnitMarshaller implements XMarshaller<XEmissionsUnit, EmissionsUnit> {
    private String programSystemCode;
    private XFacilitySiteComponentIdentifiersMarshaller xFacilitySiteComponentIdentifiersMarshaller;
    private XRegulationMarshaller xRegulationMarshaller;
    private XEmissionProcessMarshaller xEmissionProcessMarshaller;

    public XEmissionUnitMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteComponentIdentifiersMarshaller = new XFacilitySiteComponentIdentifiersMarshaller(this.programSystemCode);
        this.xRegulationMarshaller = new XRegulationMarshaller();
        this.xEmissionProcessMarshaller = new XEmissionProcessMarshaller(this.programSystemCode);
    }

    public XEmissionsUnit marshall(final EmissionsUnit emissionUnit) {
        if (emissionUnit.getEisEmissionsUnitId() == null)
            return null;

        XEmissionsUnit xEmissionsUnit = new XEmissionsUnit();
        xEmissionsUnit.setUnitDescription(StringUtils.isNotBlank(emissionUnit.getDescription()) ? emissionUnit.getDescription() : null);
        xEmissionsUnit.setUnitTypeCode(XMarshallerUtility.getCode(emissionUnit.getUnitTypeCode()));
        if (emissionUnit.getDesignCapacity() != null) {
            xEmissionsUnit.setUnitDesignCapacity(emissionUnit.getDesignCapacity().getDesignCapacity() != null ? emissionUnit.getDesignCapacity().getDesignCapacity().getValue() : null);
            xEmissionsUnit.setUnitDesignCapacityUnitofMeasureCode(XMarshallerUtility.getCode(emissionUnit.getDesignCapacity().getDesignCapacityUnitOfMeasureCode()));
        }
        xEmissionsUnit.setUnitStatusCode(XMarshallerUtility.getCode(emissionUnit.getUnitStatusCode()));
        xEmissionsUnit.setUnitStatusCodeYear(emissionUnit.getUnitStatusCodeYear() != null ? emissionUnit.getUnitStatusCodeYear().getValue() : null);
        xEmissionsUnit.setUnitOperationDate(emissionUnit.getUnitOperationDate() != null ? emissionUnit.getUnitOperationDate().getXmlDate() : null);
        xEmissionsUnit.setUnitComment(StringUtils.isNotBlank(emissionUnit.getEisComment()) ? emissionUnit.getEisComment() : null);

        //Add EIS and Alternate Identifiers
        xEmissionsUnit.getUnitIdentifications().addAll(
                xFacilitySiteComponentIdentifiersMarshaller.marshall(emissionUnit.getEisEmissionsUnitId().toString(), emissionUnit.getAlternateIdentifiers())
        );

        //Add regulations
        List<XRegulationDataType> xRegulationDataTypes = xEmissionsUnit.getUnitRegulations();
        emissionUnit.getRegulations().stream()
                .forEach(r -> xRegulationDataTypes.add(xRegulationMarshaller.marshall(r)));

        //Add processes
        List<XProcessDataType> xProcesses = xEmissionsUnit.getUnitEmissionsProcess();
        emissionUnit.getProcesses().stream().forEach(ep -> xProcesses.add(xEmissionProcessMarshaller.marshall(ep)));

        return xEmissionsUnit;
    }
}