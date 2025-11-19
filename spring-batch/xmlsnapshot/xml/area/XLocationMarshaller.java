package gov.epa.eis.batch.xmlsnapshot.xml.area;

import gov.epa.eis.batch.cers.XLocation;
import gov.epa.eis.batch.cers.XProcessDataType;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.dto.LocationDto;

import java.util.List;

/**
 * XLocationMarshaller
 */
public class XLocationMarshaller implements XMarshaller<XLocation, LocationDto> {
    private XEmissionProcessMarshaller xEmissionProcessMarshaller = new XEmissionProcessMarshaller();

    public XLocationMarshaller() {
        this.xEmissionProcessMarshaller = new XEmissionProcessMarshaller();
    }

    public XLocation marshall(final LocationDto locationDto) {
        XLocation xLocation = new XLocation();

        if (locationDto.getCensusTract() != null) {
            xLocation.setCensusTractIdentifier(locationDto.getCensusTract() != null ? locationDto.getCensusTract().getCode() : null);
        } else if (locationDto.getShapeId() != null) {
            xLocation.setShapeIdentifier(locationDto.getShapeId() != null ? locationDto.getShapeId() : null);
        } else if (locationDto.getCounty() != null) {
            xLocation.setStateAndCountyFIPSCode(locationDto.getCounty() != null ? locationDto.getCounty().getCode() : null);
        } else if (locationDto.getTribalCode() != null) {
            xLocation.setTribalCode(locationDto.getTribalCode() != null ? locationDto.getTribalCode().getCode() : null);
        }

        //Not used currently
        //      List<XExcludedLocationParameter> xExcludedLocationParameters = xLocation.getExcludedLocationParameters();

        List<XProcessDataType> xEmissionsProcesses = xLocation.getLocationEmissionsProcess();
        locationDto.getEmissionsProcesses().stream()
                .forEach(ep -> xEmissionsProcesses.add(xEmissionProcessMarshaller.marshall(ep)));

        return xLocation;
    }

}