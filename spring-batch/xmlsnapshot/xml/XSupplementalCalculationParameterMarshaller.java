package gov.epa.eis.batch.xmlsnapshot.xml;

import gov.epa.eis.batch.cers.XSupplementalCalculationParameter;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.SupplementalCalculationParameter;
import org.apache.commons.lang3.StringUtils;

/**
 * XSupplementalCalculationParameterMarshaller
 */
public class XSupplementalCalculationParameterMarshaller implements XMarshaller<XSupplementalCalculationParameter, SupplementalCalculationParameter> {
    public XSupplementalCalculationParameter marshall(final SupplementalCalculationParameter supplementalCalculationParameter) {
        XSupplementalCalculationParameter xSupplementalCalculationParameter = new XSupplementalCalculationParameter();
        xSupplementalCalculationParameter.setSupplementalCalculationParameterType(supplementalCalculationParameter.getType() != null ? supplementalCalculationParameter.getType() : null);
        // One or the other of these is not null.  Value or HeatValue.
        // Both are written to SupplementalCalculationParameterValue.
        if (supplementalCalculationParameter.getValue() != null)
            xSupplementalCalculationParameter.setSupplementalCalculationParameterValue(supplementalCalculationParameter.getValue().getValue());
        else if (supplementalCalculationParameter.getHeatValue() != null)
            xSupplementalCalculationParameter.setSupplementalCalculationParameterValue(supplementalCalculationParameter.getHeatValue().getRoundedValue(5));
        xSupplementalCalculationParameter.setSupplementalCalculationParameterNumeratorUnitofMeasureCode(XMarshallerUtility.getCode(supplementalCalculationParameter.getNumeratorUnitOfMeasureCode()));
        xSupplementalCalculationParameter.setSupplementalCalculationParameterDenominatorUnitofMeasureCode(XMarshallerUtility.getCode(supplementalCalculationParameter.getDenominatorUnitOfMeasureCode()));
        xSupplementalCalculationParameter.setSupplementalCalculationParameterDataYear(supplementalCalculationParameter.getDataYear() != null ? supplementalCalculationParameter.getDataYear().getValue() : null);
        xSupplementalCalculationParameter.setSupplementalCalculationParameterDataSource(StringUtils.isNotBlank(supplementalCalculationParameter.getDataSource()) ? supplementalCalculationParameter.getDataSource() : null);
        xSupplementalCalculationParameter.setSupplementalCalculationParameterComment(StringUtils.isNotBlank(supplementalCalculationParameter.getComment()) ? (supplementalCalculationParameter.getComment()) : null);
        return xSupplementalCalculationParameter;
    }
}