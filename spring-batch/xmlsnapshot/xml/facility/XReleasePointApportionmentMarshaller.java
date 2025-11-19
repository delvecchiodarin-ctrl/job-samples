package gov.epa.eis.batch.xmlsnapshot.xml.facility;

import gov.epa.eis.batch.cers.XReleasePointApportionment;
import gov.epa.eis.batch.xmlsnapshot.xml.XFacilitySiteComponentIdentifiersMarshaller;
import gov.epa.eis.batch.xmlsnapshot.xml.XMarshaller;
import gov.epa.eis.model.ReleasePointApportionment;
import org.apache.commons.lang3.StringUtils;

/**
 * XReleasePointApportionmentMarshaller
 */
public class XReleasePointApportionmentMarshaller implements XMarshaller<XReleasePointApportionment, ReleasePointApportionment> {
    private String programSystemCode;
    private XFacilitySiteComponentIdentifiersMarshaller xFacilitySiteComponentIdentifiersMarshaller;

    public XReleasePointApportionmentMarshaller(final String programSystemCode) {
        this.programSystemCode = programSystemCode;
        this.xFacilitySiteComponentIdentifiersMarshaller
                = new XFacilitySiteComponentIdentifiersMarshaller(this.programSystemCode);
    }

    public XReleasePointApportionment marshall(final ReleasePointApportionment releasePointApportionment) {
        XReleasePointApportionment xReleasePointApportionment = new XReleasePointApportionment();
        xReleasePointApportionment.setAveragePercentEmissions(releasePointApportionment.getAveragePercentEmissions() != null ? (releasePointApportionment.getAveragePercentEmissions().getValue()) : null);
        xReleasePointApportionment.setReleasePointApportionmentIsUncontrolled(releasePointApportionment.getUncontrolled() != null && releasePointApportionment.getUncontrolled() ? "Yes" : "No");
        xReleasePointApportionment.setReleasePointApportionmentComment(StringUtils.isNotBlank(releasePointApportionment.getEisComment()) ? releasePointApportionment.getEisComment() : null);

        //Add EIS and Alternate Identifiers for Release Point
        xReleasePointApportionment.getReleasePointApportionmentIdentifications().addAll(
                xFacilitySiteComponentIdentifiersMarshaller.marshall(releasePointApportionment.getReleasePoint().getEisReleasePointId().toString(), releasePointApportionment.getReleasePoint().getAlternateIdentifiers())
        );

        //Add EIS and Alternate Identifiers for Site Path
        if (releasePointApportionment.getSiteControlPath() != null)
            xReleasePointApportionment.getReleasePointApportionmentPathIdentifications().addAll(
                    xFacilitySiteComponentIdentifiersMarshaller.marshall(releasePointApportionment.getSiteControlPath().getEisPathId().toString(), releasePointApportionment.getSiteControlPath().getAlternateIdentifiers())
            );

        return xReleasePointApportionment;
    }
}