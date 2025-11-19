package gov.epa.eis.batch.xmlsnapshot.xml;

import gov.epa.eis.batch.cers.XOperatingDetails;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.embeddable.OperatingDetails;

/**
 * XOperatingDetailsMarshaller
 */
public class XOperatingDetailsMarshaller implements XMarshaller<XOperatingDetails, OperatingDetails> {
    public XOperatingDetails marshall(final OperatingDetails operatingDetails) {
        XOperatingDetails xOperatingDetails = new XOperatingDetails();
        xOperatingDetails.setActualHoursPerPeriod(operatingDetails.getActualHoursPerPeriod() != null ? operatingDetails.getActualHoursPerPeriod().getValue() : null);
        xOperatingDetails.setAverageDaysPerWeek(operatingDetails.getAverageDaysPerWeek() != null ? operatingDetails.getAverageDaysPerWeek().getValue() : null);
        xOperatingDetails.setAverageHoursPerDay(operatingDetails.getAverageHoursPerDay() != null ? operatingDetails.getAverageHoursPerDay().getValue() : null);
        xOperatingDetails.setAverageWeeksPerPeriod(operatingDetails.getAverageWeeksPerPeriod() != null ? operatingDetails.getAverageWeeksPerPeriod().getValue() : null);
        xOperatingDetails.setPercentWinterActivity(operatingDetails.getPercentWinterActivity() != null ? (operatingDetails.getPercentWinterActivity().getValue()) : null);
        xOperatingDetails.setPercentSpringActivity(operatingDetails.getPercentSpringActivity() != null ? (operatingDetails.getPercentSpringActivity().getValue()) : null);
        xOperatingDetails.setPercentSummerActivity(operatingDetails.getPercentSummerActivity() != null ? (operatingDetails.getPercentSummerActivity().getValue()) : null);
        xOperatingDetails.setPercentFallActivity(operatingDetails.getPercentFallActivity() != null ? (operatingDetails.getPercentFallActivity().getValue()) : null);
        return xOperatingDetails;
    }
}