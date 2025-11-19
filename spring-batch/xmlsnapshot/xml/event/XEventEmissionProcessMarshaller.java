package gov.epa.eis.batch.xmlsnapshot.xml.event;

import gov.epa.eis.batch.cers.XEmissionsDataType;
import gov.epa.eis.batch.cers.XEventEmissionsProcess;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.EventProcess;

import java.util.List;

/**
 * XEventEmissionProcessMarshaller
 */
public class XEventEmissionProcessMarshaller implements XMarshaller<XEventEmissionsProcess, EventProcess> {
    private XEventEmissionMarshaller xEventEmissionMarshaller;

    public XEventEmissionProcessMarshaller() {
        this.xEventEmissionMarshaller = new XEventEmissionMarshaller();
    }

    public XEventEmissionsProcess marshall(final EventProcess eventProcess) {
        XEventEmissionsProcess xEventEmissionsProcess = new XEventEmissionsProcess();

        xEventEmissionsProcess.setSourceClassificationCode(XMarshallerUtility.getCode(eventProcess.getSourceClassificationCode()));
        xEventEmissionsProcess.setAmountofFuelConsumed(eventProcess.getAmountOfFuelConsumed() != null ? eventProcess.getAmountOfFuelConsumed().getValue() : null);
        xEventEmissionsProcess.setAmountofFuelConsumedUnitofMeasureCode(XMarshallerUtility.getCode(eventProcess.getAmountOfFuelConsumedUnitOfMeasureCode()));
        xEventEmissionsProcess.setEmissionReductionTechniqueCode(XMarshallerUtility.getCode(eventProcess.getEmissionReductionTechniqueCode()));
        xEventEmissionsProcess.setFuelConfigurationCode(XMarshallerUtility.getCode(eventProcess.getFuelConfigurationCode()));
        xEventEmissionsProcess.setFuelLoading(eventProcess.getFuelLoading() != null ? eventProcess.getFuelLoading().getValue() : null);
        xEventEmissionsProcess.setFuelLoadingUnitofMeasureCode(XMarshallerUtility.getCode(eventProcess.getFuelLoadingUnitOfMeasureCode()));
        xEventEmissionsProcess.setHeatRelease(eventProcess.getHeatRelease() != null ? eventProcess.getHeatRelease().getValue() : null);
        xEventEmissionsProcess.setHeatReleaseUnitofMeasureCode(XMarshallerUtility.getCode(eventProcess.getHeatReleaseUnitOfMeasureCode()));
        // xEventEmissionsProcess.setPercentDuffFuelMoisture(eventProcess.getPercentDuffFuelMoisture() != null ? new BigDecimal(eventProcess.getPercentDuffFuelMoisture().getValue()) : null);
        //  xEventEmissionsProcess.setPercentLiveFuelMoisture(eventProcess.getPercentLiveFuelMoisture() != null ? new BigDecimal(eventProcess.getPercentLiveFuelMoisture().getValue()) : null);
        //  xEventEmissionsProcess.setPercentOneThousandHourFuelMoisture(eventProcess.getPercentOneThousandHourFuelMoisture() != null ? new BigDecimal(eventProcess.getPercentOneThousandHourFuelMoisture().getValue()) : null);
        //  xEventEmissionsProcess.setPercentTenHourFuelMoisture(eventProcess.getPercentTenHourFuelMoisture() != null ? new BigDecimal(eventProcess.getPercentTenHourFuelMoisture().getValue()) : null);
        xEventEmissionsProcess.setEventEmissionsProcessComment(eventProcess.getEventProcessComment());

        //Add Emissions
        List<XEmissionsDataType> xEmissionsDataTypes = xEventEmissionsProcess.getEventEmissionsProcessEmissions();
        eventProcess.getEventEmissions().stream()
                .forEach(e -> xEmissionsDataTypes.add(xEventEmissionMarshaller.marshall(e)));

        return xEventEmissionsProcess;
    }
}