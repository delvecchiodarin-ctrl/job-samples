package gov.epa.eis.batch.xmlsnapshot.xml.area;

import gov.epa.eis.batch.cers.XControlMeasure;
import gov.epa.eis.batch.cers.XControlPollutantDataType;
import gov.epa.eis.batch.cers.XProcessControlApproach;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.ControlApproach;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * XControlApproachMarshaller
 */
public class XControlApproachMarshaller implements XMarshaller<XProcessControlApproach, ControlApproach> {
    private XControlMeasureMarshaller xControlMeasureMarshaller;
    private XControlPollutantMarshaller xControlPollutantMarshaller;

    public XControlApproachMarshaller() {
        this.xControlMeasureMarshaller = new XControlMeasureMarshaller();
        this.xControlPollutantMarshaller = new XControlPollutantMarshaller();
    }

    public XProcessControlApproach marshall(final ControlApproach controlApproach) {
        XProcessControlApproach xControlApproachDataType = new XProcessControlApproach();
        xControlApproachDataType.setControlApproachDescription(StringUtils.isNotBlank(controlApproach.getDescription()) ? controlApproach.getDescription() : null);
        xControlApproachDataType.setPercentControlApproachCaptureEfficiency(controlApproach.getPercentCaptureEfficiency() != null ? controlApproach.getPercentCaptureEfficiency().getValue() : null);
        xControlApproachDataType.setPercentControlApproachPenetration(controlApproach.getPercentPenetration() != null ? controlApproach.getPercentPenetration().getValue() : null);
        xControlApproachDataType.setFirstInventoryYear(controlApproach.getFirstInventoryYear() != null ? controlApproach.getFirstInventoryYear().getValue() : null);
        xControlApproachDataType.setLastInventoryYear(controlApproach.getLastInventoryYear() != null ? controlApproach.getLastInventoryYear().getValue() : null);
        xControlApproachDataType.setControlApproachComment(StringUtils.isNotBlank(controlApproach.getEisComment()) ? (controlApproach.getEisComment()) : null);

        List<XControlMeasure> xControlMeasures = xControlApproachDataType.getControlMeasures();
        controlApproach.getControlMeasures().stream()
                .forEach(cm -> xControlMeasures.add(xControlMeasureMarshaller.marshall(cm)));

        List<XControlPollutantDataType> xControlPollutants = xControlApproachDataType.getControlPollutants();
        controlApproach.getControlPollutants().stream()
                .forEach(cp -> xControlPollutants.add(xControlPollutantMarshaller.marshall(cp)));

        return xControlApproachDataType;
    }
}