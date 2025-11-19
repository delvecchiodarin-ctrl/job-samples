package gov.epa.eis.batch.xmlsnapshot.xml;

import gov.epa.eis.batch.cers.XGeographicCoordinatesDataType;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.GeographicCoordinate;
import gov.epa.eis.model.code.UnitMeasureCode;

/**
 * XGeographicCoordinateMarshaller
 */
public class XGeographicCoordinateMarshaller implements XMarshaller<XGeographicCoordinatesDataType, GeographicCoordinate> {
    public XGeographicCoordinatesDataType marshall(final GeographicCoordinate geographicCoordinate) {
        XGeographicCoordinatesDataType xGeographicCoordinatesDataType = new XGeographicCoordinatesDataType();
        xGeographicCoordinatesDataType.setLatitudeMeasure(geographicCoordinate.getLatitudeMeasure() != null ? geographicCoordinate.getLatitudeMeasure().getValue() : null);
        xGeographicCoordinatesDataType.setLongitudeMeasure(geographicCoordinate.getLongitudeMeasure() != null ? geographicCoordinate.getLongitudeMeasure().getValue() : null);
        xGeographicCoordinatesDataType.setSourceMapScaleNumber(geographicCoordinate.getSourceMapScaleNumber() != null ? geographicCoordinate.getSourceMapScaleNumber().getValue() : null);
        if (geographicCoordinate.getHorizontalAccuracyMeasure() != null) {
            xGeographicCoordinatesDataType.setHorizontalAccuracyMeasure(geographicCoordinate.getHorizontalAccuracyMeasure() != null ? geographicCoordinate.getHorizontalAccuracyMeasure().getValue() : null);
            xGeographicCoordinatesDataType.setHorizontalAccuracyUnitofMeasure(UnitMeasureCode.METERS);
        }
        xGeographicCoordinatesDataType.setHorizontalCollectionMethodCode(XMarshallerUtility.getCode(geographicCoordinate.getHorizontalCollectionMethodCode()));
        xGeographicCoordinatesDataType.setHorizontalReferenceDatumCode(XMarshallerUtility.getCode(geographicCoordinate.getHorizontalReferenceDatumCode()));
        xGeographicCoordinatesDataType.setGeographicReferencePointCode(XMarshallerUtility.getCode(geographicCoordinate.getGeographicReferencePointCode()));
        xGeographicCoordinatesDataType.setDataCollectionDate(geographicCoordinate.getDataCollectionDate() != null ? geographicCoordinate.getDataCollectionDate().getXmlDate() : null);
        xGeographicCoordinatesDataType.setGeographicComment(geographicCoordinate.getEisComment());
        if (geographicCoordinate.getVerticalMeasure() != null) {
            xGeographicCoordinatesDataType.setVerticalMeasure(geographicCoordinate.getVerticalMeasure() != null ? geographicCoordinate.getVerticalMeasure().getValue() : null);
            xGeographicCoordinatesDataType.setVerticalUnitofMeasureCode(UnitMeasureCode.METERS);
        }
        xGeographicCoordinatesDataType.setVerticalCollectionMethodCode(XMarshallerUtility.getCode(geographicCoordinate.getVerticalCollectionMethodCode()));
        xGeographicCoordinatesDataType.setVerticalReferenceDatumCode(XMarshallerUtility.getCode(geographicCoordinate.getVerticalReferenceDatumCode()));
        xGeographicCoordinatesDataType.setVerificationMethodCode(XMarshallerUtility.getCode(geographicCoordinate.getVerificationMethodCode()));
        xGeographicCoordinatesDataType.setCoordinateDataSourceCode(XMarshallerUtility.getCode(geographicCoordinate.getCoordinateDataSourceCode()));
        xGeographicCoordinatesDataType.setGeometricTypeCode(XMarshallerUtility.getCode(geographicCoordinate.getGeometricTypeCode()));
//        xGeographicCoordinatesDataType.setAreaWithinPerimeter(geographicCoordinate.getAreaWithinPerimeter() != null ? geographicCoordinate.getAreaWithinPerimeter().getValue() : null);
//        xGeographicCoordinatesDataType.setAreaWithinPerimeterUnitofMeasureCode(XMarshallerUtility.getCode(geographicCoordinate.getAreaWithinPerimeterUnitOfMeasureCode()));
        //  xGeographicCoordinatesDataType.setPercentofAreaProducingEmissions(geographicCoordinate.getPercentOfAreaProducingEmissions() != null ? new BigDecimal(geographicCoordinate.getPercentOfAreaProducingEmissions().getValue()) : null);

        return xGeographicCoordinatesDataType;
    }
}