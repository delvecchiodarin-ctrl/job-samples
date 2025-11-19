package gov.epa.eis.batch.xmlsnapshot.xml.area;

import gov.epa.eis.batch.cers.XProcessControlApproach;
import gov.epa.eis.batch.cers.XProcessDataType;
import gov.epa.eis.batch.cers.XRegulationDataType;
import gov.epa.eis.batch.cers.XReportingPeriod;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.batch.xmlsnapshot.xml.XReportingPeriodMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.facility.XRegulationMarshaller;
import gov.epa.eis.model.EmissionsProcess;
import gov.epa.eis.model.ReportingPeriod;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * XEmissionProcessMarshaller
 */
public class XEmissionProcessMarshaller implements XMarshaller<XProcessDataType, EmissionsProcess> {
//    private XRegulationMarshaller xRegulationMarshaller;
    private XReportingPeriodMarshaller xReportingPeriodMarshaller;
//    private XControlApproachMarshaller xControlApproachMarshaller;

    public XEmissionProcessMarshaller() {
//        this.xRegulationMarshaller = new XRegulationMarshaller();
        this.xReportingPeriodMarshaller = new XReportingPeriodMarshaller();
//        this.xControlApproachMarshaller = new XControlApproachMarshaller();
    }

    public XProcessDataType marshall(final EmissionsProcess emissionProcess) {
        XProcessDataType xProcessDataType = new XProcessDataType();
        xProcessDataType.setSourceClassificationCode(XMarshallerUtility.getCode(emissionProcess.getSourceClassificationCode()));
        xProcessDataType.setEmissionsTypeCode(XMarshallerUtility.getCode(emissionProcess.getEmissionsTypeCode()));
        xProcessDataType.setAircraftEngineTypeCode(XMarshallerUtility.getCode(emissionProcess.getAircraftEngineTypeCode()));
        // xProcessDataType.setLastEmissionsYear(emissionProcess.getLastInventoryYear() != null ? emissionProcess.getLastInventoryYear().getValue() : null);
        xProcessDataType.setProcessComment(StringUtils.isNotBlank(emissionProcess.getEisComment()) ? (emissionProcess.getEisComment()) : null);
        xProcessDataType.setProcessDescription(StringUtils.isNotBlank(emissionProcess.getDescription()) ? (emissionProcess.getDescription()) : null);

//Not needed right, really an ownership issue, whose reg is it?
//        //add regulations
//        List<XRegulationDataType> xRegulationDataTypes = xProcessDataType.getProcessRegulations();
//        emissionProcess.getRegulations().stream()
//                .forEach(r -> xRegulationDataTypes.add(xRegulationMarshaller.marshall(r)));

//Not needed right, really an ownership issue, whose control approach is it?
//        List<XProcessControlApproach> xProcessControlApproaches = xProcessDataType.getProcessControlApproaches();
//        emissionProcess.getControlApproaches().stream()
//                .filter(ca -> ca.isActive())
//                .forEach(ca -> xProcessControlApproaches.add(xControlApproachMarshaller.marshall(ca)));

        List<XReportingPeriod> xReportingPeriods = xProcessDataType.getReportingPeriods();
        emissionProcess.getReportingPeriods().stream()
                .forEach(rp -> xReportingPeriods.add(xReportingPeriodMarshaller.marshall(rp)));

        return xProcessDataType;
    }
}