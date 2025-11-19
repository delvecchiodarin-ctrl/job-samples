package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XControlPathDefinition;
import gov.epa.eis.batch.cers.XControlPollutantDataType;
import gov.epa.eis.batch.cers.XFacilitySitePath;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteComponentIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.PathAssignment;
import gov.epa.eis.model.SiteControlPath;
import gov.epa.eis.model.SiteControlPollutant;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * XSitePathMarshaller
 */
public class XSitePathMarshaller implements XMarshaller<XFacilitySitePath, SiteControlPath> {
    private String programSystemCode;
    private XFacilitySiteComponentIdentifiersMarshaller xFacilitySiteComponentIdentifiersMarshaller;
    private XSitePathDefinitionMarshaller xSitePathDefinitionMarshaller;
    private XControlPollutantMarshaller xControlPollutantMarshaller;

    public XSitePathMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteComponentIdentifiersMarshaller = new XFacilitySiteComponentIdentifiersMarshaller(this.programSystemCode);
        this.xSitePathDefinitionMarshaller = new XSitePathDefinitionMarshaller(this.programSystemCode);
        this.xControlPollutantMarshaller = new XControlPollutantMarshaller();
    }

    public XFacilitySitePath marshall(final SiteControlPath siteControlPath) {
        if (siteControlPath.getEisPathId() == null)
            return null;

        XFacilitySitePath xFacilitySitePath = new XFacilitySitePath();
        xFacilitySitePath.setPathName(StringUtils.isNotBlank(siteControlPath.getPathName()) ? siteControlPath.getPathName() : null);
        xFacilitySitePath.setPathDescription(siteControlPath.getPathDescription() != null ? siteControlPath.getPathDescription() : null);
        xFacilitySitePath.setPercentPathEffectiveness(siteControlPath.getPercentEffectiveness() != null ? siteControlPath.getPercentEffectiveness().getValue() : null);

        //Add EIS and Alternate Identifiers
        xFacilitySitePath.getControlPathIdentifications().addAll(
                xFacilitySiteComponentIdentifiersMarshaller.marshall(siteControlPath.getEisPathId().toString(), siteControlPath.getAlternateIdentifiers())
        );

        //Add Path Definitions
        List<XControlPathDefinition> xControlPathDefinitions = xFacilitySitePath.getControlPathDefinitions();
        siteControlPath.getPathAssignments().stream()
                .forEach(pa -> xControlPathDefinitions.add(xSitePathDefinitionMarshaller.marshall(pa)));

        //Add Control Pollutants
        List<XControlPollutantDataType> xControlPollutants = xFacilitySitePath.getControlPathControlPollutants();
        siteControlPath.getSiteControlPollutants().stream()
                .forEach(cp -> xControlPollutants.add(xControlPollutantMarshaller.marshall(cp)));

        return xFacilitySitePath;
    }
}