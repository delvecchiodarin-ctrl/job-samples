package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XControlPollutantDataType;
import gov.epa.eis.batch.cers.XFacilitySiteControl;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteComponentIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshallerUtility;
import gov.epa.eis.model.SiteControlPollutant;
import gov.epa.eis.model.SiteControls;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * XSiteControlMarshaller
 */
public class XSiteControlMarshaller implements XMarshaller<XFacilitySiteControl, SiteControls> {
    private String programSystemCode;
    private XFacilitySiteComponentIdentifiersMarshaller xFacilitySiteComponentIdentifiersMarshaller;
    private XControlPollutantMarshaller xControlPollutantMarshaller;

    public XSiteControlMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteComponentIdentifiersMarshaller = new XFacilitySiteComponentIdentifiersMarshaller(this.programSystemCode);
        this.xControlPollutantMarshaller = new XControlPollutantMarshaller();
    }

    public XFacilitySiteControl marshall(final SiteControls siteControl) {
        if (siteControl.getEisSiteControlId() == null)
            return null;

        XFacilitySiteControl xFacilitySiteControl = new XFacilitySiteControl();
        xFacilitySiteControl.setControlDescription(StringUtils.isNotBlank(siteControl.getDescription()) ? siteControl.getDescription() : null);
        xFacilitySiteControl.setControlComment(siteControl.getComment() != null ? siteControl.getComment() : null);
        xFacilitySiteControl.setControlEndDate(siteControl.getEndDate() != null ? siteControl.getEndDate().toString() : null);
        xFacilitySiteControl.setControlMeasureCode(XMarshallerUtility.getCode(siteControl.getControlMeasureCode()));
        xFacilitySiteControl.setControlNumberOperatingMonths(siteControl.getNoOfOperatingMonths() != null ? siteControl.getNoOfOperatingMonths().getValue() : null);
        xFacilitySiteControl.setControlStartDate(siteControl.getStartDate() != null ? siteControl.getStartDate().toString() : null);
        xFacilitySiteControl.setControlEndDate(siteControl.getEndDate() != null ? siteControl.getEndDate().toString() : null);
        xFacilitySiteControl.setControlStatusCode(XMarshallerUtility.getCode(siteControl.getControlStatusCode()));
        xFacilitySiteControl.setControlStatusCodeYear(siteControl.getControlStatusCodeYear() != null ? siteControl.getControlStatusCodeYear().getValue() : null);
        xFacilitySiteControl.setControlUpgradeDate(siteControl.getUpgradeDate() != null ? siteControl.getUpgradeDate().getDate() : null);
        xFacilitySiteControl.setControlUpgradeDescription(siteControl.getUpgradeDescriptiony() != null ? siteControl.getUpgradeDescriptiony() : null);
        xFacilitySiteControl.setPercentControlEffectiveness(siteControl.getPercentEffectiveness() != null ? siteControl.getPercentEffectiveness().getValue() : null);

        //Add EIS and Alternate Identifiers
        xFacilitySiteControl.getSiteControlIdentifications().addAll(
                xFacilitySiteComponentIdentifiersMarshaller.marshall(siteControl.getEisSiteControlId().toString(), siteControl.getAlternateIdentifiers())
        );

        //Add Control Pollutants
        List<XControlPollutantDataType> xControlPollutants = xFacilitySiteControl.getSiteControlPollutants();
        siteControl.getSiteControlPollutants().stream()
                .forEach(cp -> xControlPollutants.add(xControlPollutantMarshaller.marshall(cp)));

        return xFacilitySiteControl;
    }
}