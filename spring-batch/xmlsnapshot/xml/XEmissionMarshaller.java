package gov.epa.eis.batch.xmlsnapshot.xml;

import gov.epa.eis.batch.cers.XEmissionsDataType;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.Emissions;
import org.apache.commons.lang3.StringUtils;

/**
 * XEmissionMarshaller
 */
public class XEmissionMarshaller implements XMarshaller<XEmissionsDataType, Emissions> {
    public XEmissionsDataType marshall(final Emissions emission) {
        XEmissionsDataType xEmissionsDataType = new XEmissionsDataType();
        xEmissionsDataType.setPollutantCode(XMarshallerUtility.getCode(emission.getPollutantCode()));
        xEmissionsDataType.setTotalEmissions(emission.getTotalEmissions() != null ? emission.getTotalEmissions().getValue() : null);
        xEmissionsDataType.setEmissionsUnitofMeasureCode(XMarshallerUtility.getCode(emission.getEmissionsUnitOfMeasureCode()));
        xEmissionsDataType.setEmissionFactor(emission.getEmissionFactor() != null ? emission.getEmissionFactor().getRoundedValue(4) : null);
        xEmissionsDataType.setEmissionFactorNumeratorUnitofMeasureCode(XMarshallerUtility.getCode(emission.getEmissionFactorNumeratorUnitOfMeasureCode()));
        xEmissionsDataType.setEmissionFactorDenominatorUnitofMeasureCode(XMarshallerUtility.getCode(emission.getEmissionFactorDenominatorUnitOfMeasureCode()));
        xEmissionsDataType.setEmissionFactorText(StringUtils.isNotBlank(emission.getEmissionFactorText()) ? (emission.getEmissionFactorText()) : null);
        xEmissionsDataType.setEmissionCalculationMethodCode(XMarshallerUtility.getCode(emission.getEmissionsCalculationMethodCode()));
        xEmissionsDataType.setEmissionsComment(StringUtils.isNotBlank(emission.getComment()) ? (emission.getComment()) : null);
        return xEmissionsDataType;
    }
}