package gov.epa.eis.batch.frs.reader;

import gov.epa.eis.model.view.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by DDelVecc on 4/6/2019.
 */
@Component
public class SubFacilityItemReader extends FrsHibernateCursorItemReader<SubFacilityCaerFrsView> {

    @Autowired
    public SubFacilityItemReader(SessionFactory sessionFactory) {

        super(sessionFactory, SubFacilityCaerFrsView.class);

        String hqlQueryString = "select distinct e "
                + " from " + SubFacilityCaerFrsView.class.getSimpleName() + " e"
//                + " left join e.emissionsUnits eu with eu.lastUpdatedDate > :lastPullDate"
//                + " left join e.emissionsProcesses ep with ep.lastUpdatedDate > :lastPullDate"
//                + " left join e.associations a with a.lastUpdatedDate > :lastPullDate"
                + " where e.facilitySiteId in (select eu.facilitySiteId from " + EmissionsUnitCaerFrsView.class.getSimpleName() + " eu where eu.lastUpdatedDate > :lastPullDate) "
                + " or e.facilitySiteId in (select ep.facilitySiteId from " + EmissionsProcessCaerFrsView.class.getSimpleName() + " ep where ep.lastUpdatedDate > :lastPullDate) "
                + " or e.facilitySiteId in (select rp.facilitySiteId from " + ReleasePointFrsCaerView.class.getSimpleName() + " rp where rp.lastUpdatedDate > :lastPullDate) "
                + " or e.facilitySiteId in (select sc.facilitySiteId from " + SiteControlCaerFrsView.class.getSimpleName() + " sc where sc.lastUpdatedDate > :lastPullDate) "
                + " or e.facilitySiteId in (select sp.facilitySiteId from " + SitePathCaerFrsView.class.getSimpleName() + " sp where sp.lastUpdatedDate > :lastPullDate) "
                + " or e.facilitySiteId in (select pd.facilitySiteId from " + PathDefinitionCaerFrsView.class.getSimpleName() + " pd where pd.lastUpdatedDate > :lastPullDate) "
                + " or e.facilitySiteId in (select a.facilitySiteId from " + AssociationCaerFrsView.class.getSimpleName() + " a where a.lastUpdatedDate > :lastPullDate) ";

        this.setQueryString(hqlQueryString);
 //        sessionFactory.getCurrentSession().enableFilter("lastUpdatedDateFilter");

    }

    @Override
    public SubFacilityCaerFrsView doRead() throws Exception {
        SubFacilityCaerFrsView s = super.doRead();

        if (s == null)
            return s;

        //filter processes by lastUpdatedDate
        final Set<EmissionsUnitCaerFrsView> units = s.getEmissionsUnits();
        if (units != null) {
            Set<EmissionsUnitCaerFrsView> newUnits = new HashSet<>();
            units.stream().filter(u->u.getLastUpdatedDate().after(lastPullDate)).forEach(u -> newUnits.add(u));
            units.clear();
            units.addAll(newUnits);
        }

        //filter processes by lastUpdatedDate
        final Set<EmissionsProcessCaerFrsView> processes = s.getEmissionsProcesses();
        if (processes != null) {
            Set<EmissionsProcessCaerFrsView> newProcesses = new HashSet<>();
            processes.stream().filter(p->p.getLastUpdatedDate().after(lastPullDate)).forEach(p -> newProcesses.add(p));
            processes.clear();
            processes.addAll(newProcesses);
        }

        //filter site controls by lastUpdatedDate
        final Set<SiteControlCaerFrsView> siteControls = s.getSiteControls();
        if (siteControls != null) {
            Set<SiteControlCaerFrsView> newSiteControls = new HashSet<>();
            siteControls.stream().filter(p->p.getLastUpdatedDate().after(lastPullDate)).forEach(p -> newSiteControls.add(p));
            siteControls.clear();
            siteControls.addAll(newSiteControls);
        }

        //filter site paths by lastUpdatedDate
        final Set<SitePathCaerFrsView> sitePaths = s.getSitePaths();
        if (sitePaths != null) {
            Set<SitePathCaerFrsView> newSitePaths = new HashSet<>();
            sitePaths.stream().filter(p->p.getLastUpdatedDate().after(lastPullDate)).forEach(p -> newSitePaths.add(p));
            sitePaths.clear();
            sitePaths.addAll(newSitePaths);
        }

        //filter path definitions by lastUpdatedDate
        final Set<PathDefinitionCaerFrsView> pathDefinitions = s.getPathDefinitions();
        if (pathDefinitions != null) {
            Set<PathDefinitionCaerFrsView> newPathDefinitions = new HashSet<>();
            pathDefinitions.stream().filter(p->p.getLastUpdatedDate().after(lastPullDate)).forEach(p -> newPathDefinitions.add(p));
            pathDefinitions.clear();
            pathDefinitions.addAll(newPathDefinitions);
        }

        //filter release points by lastUpdatedDate
        final Set<ReleasePointFrsCaerView> releasePoints = s.getReleasePoints();
        if (releasePoints != null) {
            Set<ReleasePointFrsCaerView> newReleasePoints = new HashSet<>();
            releasePoints.stream().filter(rp->rp.getLastUpdatedDate().after(lastPullDate)).forEach(rp -> newReleasePoints.add(rp));
            releasePoints.clear();
            releasePoints.addAll(newReleasePoints);
        }

        //filter associations by lastUpdatedDate
        final Set<AssociationCaerFrsView> associations = s.getAssociations();
        if (associations != null) {
            Set<AssociationCaerFrsView> newAssociations = new HashSet<>();
            associations.stream().filter(a->a.getLastUpdatedDate().after(lastPullDate)).forEach(a -> newAssociations.add(a));
            associations.clear();
            associations.addAll(newAssociations);
        }

        return s;
    }
}