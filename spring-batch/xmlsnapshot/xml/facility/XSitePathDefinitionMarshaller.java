package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XControlPathDefinition;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteComponentIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.PathAssignment;

/**
 * XSitePathDefinitionMarshaller
 */
public class XSitePathDefinitionMarshaller implements XMarshaller<XControlPathDefinition, PathAssignment> {
    private String programSystemCode;
    private XFacilitySiteComponentIdentifiersMarshaller xFacilitySiteComponentIdentifiersMarshaller;

    public XSitePathDefinitionMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteComponentIdentifiersMarshaller = new XFacilitySiteComponentIdentifiersMarshaller(this.programSystemCode);
    }

    public XControlPathDefinition marshall(final PathAssignment pathAssignment) {
        XControlPathDefinition xControlPathDefinition = new XControlPathDefinition();
        xControlPathDefinition.setSequenceNumber(pathAssignment.getSequenceNum() != null ? pathAssignment.getSequenceNum().getValue() : null);
        xControlPathDefinition.setAveragePercentApportionment(pathAssignment.getAvgPctApprt() != null ? pathAssignment.getAvgPctApprt().getValue() : null);

        //Add EIS and Alternate Identifiers
        if (pathAssignment.getAssignedSiteControlPath() != null)
            xControlPathDefinition.getControlPathDefinitionPathIdentifications().addAll(
                    xFacilitySiteComponentIdentifiersMarshaller.marshall(pathAssignment.getAssignedSiteControlPath().getEisPathId().toString(), pathAssignment.getAssignedSiteControlPath().getAlternateIdentifiers())
            );
        if (pathAssignment.getAssignedSiteControl() != null)
            xControlPathDefinition.getControlPathDefinitionControlIdentifications().addAll(
                    xFacilitySiteComponentIdentifiersMarshaller.marshall(pathAssignment.getAssignedSiteControl().getEisSiteControlId().toString(), pathAssignment.getAssignedSiteControl().getAlternateIdentifiers())
            );

        return xControlPathDefinition;
    }
}