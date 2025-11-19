package gov.epa.eis.batch.xmlsnapshot.xml;

import gov.epa.eis.batch.cers.XEmissionsDataType;
import gov.epa.eis.batch.cers.XReportingPeriod;
import gov.epa.eis.batch.cers.XSupplementalCalculationParameter;
import gov.epa.eis.model.ReportingPeriod;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * XReportingPeriodMarshaller
 */
public class XReportingPeriodMarshaller implements XMarshaller<XReportingPeriod, ReportingPeriod> {
    private XOperatingDetailsMarshaller xOperatingDetailsMarshaller;
    private XSupplementalCalculationParameterMarshaller xSupplementalCalculationParameterMarshaller;
    private XEmissionMarshaller xEmissionMarshaller;

    public XReportingPeriodMarshaller() {
        this.xOperatingDetailsMarshaller = new XOperatingDetailsMarshaller();
        this.xSupplementalCalculationParameterMarshaller = new XSupplementalCalculationParameterMarshaller();
        this.xEmissionMarshaller = new XEmissionMarshaller();
    }

    public XReportingPeriod marshall(final ReportingPeriod reportingPeriod) {
        XReportingPeriod xReportingPeriod = new XReportingPeriod();
        xReportingPeriod.setReportingPeriodTypeCode(XMarshallerUtility.getCode(reportingPeriod.getReportingPeriodTypeCode()));
        xReportingPeriod.setEmissionOperatingTypeCode(XMarshallerUtility.getCode(reportingPeriod.getEmissionsOperatingTypeCode()));
        xReportingPeriod.setStartDate(reportingPeriod.getStartDate() != null ? reportingPeriod.getStartDate().getXmlDate() : null);
        xReportingPeriod.setEndDate(reportingPeriod.getEndDate() != null ? reportingPeriod.getEndDate().getXmlDate() : null);
        xReportingPeriod.setCalculationParameterTypeCode(XMarshallerUtility.getCode(reportingPeriod.getCalculationParameterTypeCode()));
        xReportingPeriod.setCalculationParameterValue(reportingPeriod.getCalculationParameterValue() != null ? reportingPeriod.getCalculationParameterValue().getValue() : null);
        xReportingPeriod.setCalculationParameterUnitofMeasure(XMarshallerUtility.getCode(reportingPeriod.getCalculationParameterUnitOfMeasureCode()));
        xReportingPeriod.setCalculationMaterialCode(XMarshallerUtility.getCode(reportingPeriod.getCalculationMaterialCode()));
        xReportingPeriod.setCalculationDataYear(reportingPeriod.getCalculationDataYear() != null ? reportingPeriod.getCalculationDataYear().getValue() : null);
        xReportingPeriod.setCalculationDataSource(StringUtils.isNotBlank(reportingPeriod.getCalculationDataSource()) ? reportingPeriod.getCalculationDataSource() : null);
        xReportingPeriod.setReportingPeriodComment(StringUtils.isNotBlank(reportingPeriod.getComment()) ? (reportingPeriod.getComment()) : null);

        //Ad Operating Details
        if (reportingPeriod.getOperatingDetails() != null) {
            xReportingPeriod.setOperatingDetails(xOperatingDetailsMarshaller.marshall(reportingPeriod.getOperatingDetails()));
        }

        //Ad Supplemental Calculation Parameter
        List<XSupplementalCalculationParameter> xSupplementalCalculationParameters = xReportingPeriod.getSupplementalCalculationParameters();
        reportingPeriod.getSupplementalParameters().stream()
                .forEach(sp -> xSupplementalCalculationParameters.add(xSupplementalCalculationParameterMarshaller.marshall(sp)));

        //Ad Emissions
        List<XEmissionsDataType> xEmissionsDataType = xReportingPeriod.getReportingPeriodEmissions();
        reportingPeriod.getEmissions().stream()
                .forEach(e -> xEmissionsDataType.add(xEmissionMarshaller.marshall(e)));

        return xReportingPeriod;
    }
}