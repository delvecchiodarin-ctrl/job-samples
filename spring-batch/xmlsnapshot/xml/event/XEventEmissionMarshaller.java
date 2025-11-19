package gov.epa.eis.batch.xmlsnapshot.xml.event;

import gov.epa.eis.batch.cers.XEmissionsDataType;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.EventEmissions;

/**
 * XEventEmissionMarshaller
 */
public class XEventEmissionMarshaller implements XMarshaller<XEmissionsDataType, EventEmissions> {
    public XEmissionsDataType marshall(final EventEmissions eventEmission) {
        XEmissionsDataType xEmissionsDataType = new XEmissionsDataType();
        xEmissionsDataType.setPollutantCode(XMarshallerUtility.getCode(eventEmission.getPollutantCode()));
        xEmissionsDataType.setTotalEmissions(eventEmission.getTotalEmissions() != null ? eventEmission.getTotalEmissions().getValue() : null);
        xEmissionsDataType.setEmissionsUnitofMeasureCode(XMarshallerUtility.getCode(eventEmission.getEmissionsUnitOfMeasureCode()));
        xEmissionsDataType.setEmissionFactor(eventEmission.getEmissionFactor() != null ? eventEmission.getEmissionFactor().getValue() : null);
        xEmissionsDataType.setEmissionFactorNumeratorUnitofMeasureCode(XMarshallerUtility.getCode(eventEmission.getEmissionFactorNumeratorUnitOfMeasureCode()));
        xEmissionsDataType.setEmissionFactorDenominatorUnitofMeasureCode(XMarshallerUtility.getCode(eventEmission.getEmissionFactorDenominatorUnitOfMeasureCode()));
        xEmissionsDataType.setEmissionFactorText(eventEmission.getEmissionFactorText());
        xEmissionsDataType.setEmissionCalculationMethodCode(XMarshallerUtility.getCode(eventEmission.getEmissionsCalculationMethodCode()));
        xEmissionsDataType.setEmissionsComment(eventEmission.getComment());

        //TODO: XCO2Equivalent, not currently used...

        return xEmissionsDataType;
    }
}