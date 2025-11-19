package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XGeographicCoordinatesDataType;
import gov.epa.eis.batch.cers.XReleasePoint;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteComponentIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XGeographicCoordinateMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.ReleasePoint;
import gov.epa.eis.model.RpGeographicCoordinate;
import gov.epa.eis.model.code.UnitMeasureCode;
import gov.epa.eis.model.embeddable.FugitiveData;
import gov.epa.eis.model.embeddable.StackData;
import org.apache.commons.lang3.StringUtils;

/**
 * XReleasePointMarshaller
 */
public class XReleasePointMarshaller implements XMarshaller<XReleasePoint, ReleasePoint> {
    private String programSystemCode;
    private XFacilitySiteComponentIdentifiersMarshaller xFacilitySiteComponentIdentifiersMarshaller;
    private XGeographicCoordinateMarshaller xGeographicCoordinateMarshaller;

    public XReleasePointMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteComponentIdentifiersMarshaller = new XFacilitySiteComponentIdentifiersMarshaller(this.programSystemCode);
        this.xGeographicCoordinateMarshaller = new XGeographicCoordinateMarshaller();
    }

    public XReleasePoint marshall(final ReleasePoint releasePoint) {
        if (releasePoint.getEisReleasePointId() == null)
            return null;

        XReleasePoint xReleasePoint = new XReleasePoint();
        xReleasePoint.setReleasePointTypeCode(XMarshallerUtility.getCode(releasePoint.getReleasePointTypeCode()));
        xReleasePoint.setReleasePointDescription(StringUtils.isNotBlank(releasePoint.getDescription()) ? releasePoint.getDescription() : null);
        xReleasePoint.setReleasePointComment(StringUtils.isNotBlank(releasePoint.getEisComment()) ? releasePoint.getEisComment() : null);
        StackData stackData = releasePoint.getStack();
        if (stackData != null) {
            if (stackData.getStackHeightMeasure() != null) {
                xReleasePoint.setReleasePointStackHeightMeasure(stackData.getStackHeightMeasure().getValue());
                xReleasePoint.setReleasePointStackHeightUnitofMeasureCode(UnitMeasureCode.FEET);
            }
            if (stackData.getStackDiameterMeasure() != null) {
                xReleasePoint.setReleasePointStackDiameterMeasure(stackData.getStackDiameterMeasure().getValue());
                xReleasePoint.setReleasePointStackDiameterUnitofMeasureCode(UnitMeasureCode.FEET);
            }
        }
        xReleasePoint.setReleasePointExitGasVelocityMeasure(releasePoint.getExitGasVelocityMeasure() != null ? releasePoint.getExitGasVelocityMeasure().getValue() : null);
        xReleasePoint.setReleasePointExitGasVelocityUnitofMeasureCode(XMarshallerUtility.getCode(releasePoint.getExitGasVelocityUnitOfMeasureCode()));
        xReleasePoint.setReleasePointExitGasFlowRateMeasure(releasePoint.getExitGasFlowRateMeasure() != null ? releasePoint.getExitGasFlowRateMeasure().getValue() : null);
        xReleasePoint.setReleasePointExitGasFlowRateUnitofMeasureCode(XMarshallerUtility.getCode(releasePoint.getExitGasFlowRateUnitOfMeasureCode()));
        xReleasePoint.setReleasePointExitGasTemperatureMeasure(releasePoint.getExitGasTemperatureMeasure() != null ? releasePoint.getExitGasTemperatureMeasure().getValue() : null);
        if (releasePoint.getFenceLineDistanceMeasure() != null) {
            xReleasePoint.setReleasePointFenceLineDistanceMeasure(releasePoint.getFenceLineDistanceMeasure().getValue());
            xReleasePoint.setReleasePointFenceLineDistanceUnitofMeasureCode(UnitMeasureCode.FEET);
        }
        FugitiveData fugitiveData = releasePoint.getFugitive();
        if (fugitiveData != null) {
            if (fugitiveData.getFugitiveHeightMeasure() != null) {
                xReleasePoint.setReleasePointFugitiveHeightMeasure(fugitiveData.getFugitiveHeightMeasure().getValue());
                xReleasePoint.setReleasePointFugitiveHeightUnitofMeasureCode(UnitMeasureCode.FEET);
            }
            if (fugitiveData.getFugitiveWidthMeasure() != null) {
                xReleasePoint.setReleasePointWidthMeasure(fugitiveData.getFugitiveWidthMeasure().getValue());
                xReleasePoint.setReleasePointWidthUnitofMeasureCode(UnitMeasureCode.FEET);
            }
            if (fugitiveData.getFugitiveLengthMeasure() != null) {
                xReleasePoint.setReleasePointLengthMeasure(fugitiveData.getFugitiveLengthMeasure().getValue());
                xReleasePoint.setReleasePointLengthUnitofMeasureCode(UnitMeasureCode.FEET);
            }
			/*
			 * if (fugitiveData.getFugitiveWidthMeasure() != null) {
			 * xReleasePoint.setReleasePointFugitiveWidthMeasure(fugitiveData.
			 * getFugitiveWidthMeasure().getValue());
			 * xReleasePoint.setReleasePointFugitiveWidthUnitofMeasureCode(UnitMeasureCode.
			 * FEET); } if (fugitiveData.getFugitiveLengthMeasure() != null) {
			 * xReleasePoint.setReleasePointFugitiveLengthMeasure(fugitiveData.
			 * getFugitiveLengthMeasure().getValue());
			 * xReleasePoint.setReleasePointFugitiveLengthUnitofMeasureCode(UnitMeasureCode.
			 * FEET); }
			 */
            xReleasePoint.setReleasePointFugitiveAngleMeasure(fugitiveData.getFugitiveAngleMeasure() != null ? fugitiveData.getFugitiveAngleMeasure().getValue() : null);
        }
        xReleasePoint.setReleasePointStatusCode(XMarshallerUtility.getCode(releasePoint.getOperatingStatusCode()));
        xReleasePoint.setReleasePointStatusCodeYear(releasePoint.getStatusCodeYear() != null ? releasePoint.getStatusCodeYear().getValue() : null);

        //Add EIS and Alternate Identifiers
        xReleasePoint.getReleasePointIdentifications().addAll(
                xFacilitySiteComponentIdentifiersMarshaller.marshall(releasePoint.getEisReleasePointId().toString(), releasePoint.getAlternateIdentifiers())
        );

        XGeographicCoordinatesDataType xGeographicCoordinatesDataType = new XGeographicCoordinatesDataType();
        if (releasePoint.getGeographicCoordinate() != null) {
            xGeographicCoordinatesDataType = xGeographicCoordinateMarshaller.marshall(releasePoint.getGeographicCoordinate());
            xReleasePoint.setReleasePointGeographicCoordinates(xGeographicCoordinatesDataType);
        }

        final RpGeographicCoordinate rpGeographicCoordinate = releasePoint.getRpGeographicCoordinate();
        if (rpGeographicCoordinate != null) {
            xGeographicCoordinatesDataType.setMidPoint2LatitudeMeasure(rpGeographicCoordinate.getLatitudeMeasure() != null ? rpGeographicCoordinate.getLatitudeMeasure().getValue() : null);
            xGeographicCoordinatesDataType.setMidPoint2LongitudeMeasure(rpGeographicCoordinate.getLongitudeMeasure() != null ? rpGeographicCoordinate.getLongitudeMeasure().getValue() : null);
        }

        return xReleasePoint;
    }
}