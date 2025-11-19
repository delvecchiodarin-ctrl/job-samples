package gov.epa.eis.batch.xmlsnapshot.xml.point;

import gov.epa.eis.batch.cers.XEmissionsUnit;
import gov.epa.eis.batch.cers.XProcessDataType;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteComponentIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.EmissionsUnit;

import java.util.List;

/**
 * XEmissionUnitMarshaller
 */
public class XEmissionUnitMarshaller implements XMarshaller<XEmissionsUnit, EmissionsUnit> {
    private String programSystemCode;
    private XFacilitySiteComponentIdentifiersMarshaller xFacilitySiteComponentIdentifiersMarshaller;
    private XEmissionProcessMarshaller xEmissionProcessMarshaller;

    public XEmissionUnitMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteComponentIdentifiersMarshaller = new XFacilitySiteComponentIdentifiersMarshaller(this.programSystemCode);
        this.xEmissionProcessMarshaller = new XEmissionProcessMarshaller(this.programSystemCode);
    }

    public XEmissionsUnit marshall(final EmissionsUnit emissionUnit) {
        if (emissionUnit.getEisEmissionsUnitId() == null)
            return null;

        XEmissionsUnit xEmissionsUnit = new XEmissionsUnit();

        //Add EIS and Alternate Identifiers
        xEmissionsUnit.getUnitIdentifications().addAll(
                xFacilitySiteComponentIdentifiersMarshaller.marshall(emissionUnit.getEisEmissionsUnitId().toString(), emissionUnit.getAlternateIdentifiers())
        );

        //Add processes
        List<XProcessDataType> xProcesses = xEmissionsUnit.getUnitEmissionsProcess();
        emissionUnit.getProcesses().stream().forEach(ep -> xProcesses.add(xEmissionProcessMarshaller.marshall(ep)));

        return xEmissionsUnit;
    }
}