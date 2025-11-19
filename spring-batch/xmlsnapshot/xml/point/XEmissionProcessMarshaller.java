package gov.epa.eis.batch.xmlsnapshot.xml.point;

import gov.epa.eis.batch.cers.XProcessDataType;
import gov.epa.eis.batch.cers.XRegulationDataType;
import gov.epa.eis.batch.cers.XReleasePointApportionment;
import gov.epa.eis.batch.cers.XReportingPeriod;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteComponentIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.batch.xmlsnapshot.xml.XReportingPeriodMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.facility.XRegulationMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.facility.XReleasePointApportionmentMarshaller;
import gov.epa.eis.model.EmissionsProcess;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * XEmissionProcessMarshaller
 */
public class XEmissionProcessMarshaller implements XMarshaller<XProcessDataType, EmissionsProcess> {
    private String programSystemCode;
    private XFacilitySiteComponentIdentifiersMarshaller xFacilitySiteComponentIdentifiersMarshaller;
    private XReportingPeriodMarshaller xReportingPeriodMarshaller;

    public XEmissionProcessMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteComponentIdentifiersMarshaller
                = new XFacilitySiteComponentIdentifiersMarshaller(this.programSystemCode);
        this.xReportingPeriodMarshaller = new XReportingPeriodMarshaller();
    }

    public XProcessDataType marshall(final EmissionsProcess emissionProcess) {
        if (emissionProcess.getEisEmissionsProcessId() == null)
            return null;

        XProcessDataType xProcessDataType = new XProcessDataType();
        xProcessDataType.setSourceClassificationCode(XMarshallerUtility.getCode(emissionProcess.getSourceClassificationCode()));

        //Add EIS and Alternate Identifiers
        xProcessDataType.getProcessIdentifications().addAll(
                xFacilitySiteComponentIdentifiersMarshaller.marshall(emissionProcess.getEisEmissionsProcessId().toString(), emissionProcess.getAlternateIdentifiers())
        );

        //Add Reporting Periods
        List<XReportingPeriod> xReportingPeriods = xProcessDataType.getReportingPeriods();
        emissionProcess.getReportingPeriods().stream()
                .forEach(rp -> xReportingPeriods.add(xReportingPeriodMarshaller.marshall(rp)));

        return xProcessDataType;
    }
}