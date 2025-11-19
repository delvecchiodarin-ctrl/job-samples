package gov.epa.eis.service.impl;

import gov.epa.eis.dao.CodeTableDao;
import gov.epa.eis.model.DataCategory;
import gov.epa.eis.model.adapter.SectorCodeAdapter;
import gov.epa.eis.model.adapter.SourceClassificationCodeAdapter;
import gov.epa.eis.model.code.SectorCode;
import gov.epa.eis.model.code.SourceClassificationCode;
import gov.epa.eis.service.ApplicationPropertyService;
import gov.epa.eis.service.MailService;
import gov.epa.eis.service.SourceClassificationCodePullService;
import gov.epa.eis.service.SourceClassificationCodeWebService;
import gov.epa.eis.webservice.FacetFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SourceClassificationCodePullServiceImpl is a Spring bean singleton that can be invoked by a job scheduler.
 */
@Transactional
public final class SourceClassificationCodePullServiceImpl implements SourceClassificationCodePullService {
    private static final Log LOG = LogFactory.getLog(SourceClassificationCodePullServiceImpl.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    private SourceClassificationCodeWebService sourceClassificationCodeWebService;
    private ApplicationPropertyService applicationPropertyService;
    private CodeTableDao codeTableDao;
    private MailService mailService;


    private void logException(final Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        e.printStackTrace(writer);
        LOG.error(sw);
    }

    @Autowired
    public void setSourceClassificationCodeWebService(SourceClassificationCodeWebService sourceClassificationCodeWebService) {
        this.sourceClassificationCodeWebService = sourceClassificationCodeWebService;
    }

    @Autowired
    public void setApplicationPropertyService(ApplicationPropertyService applicationPropertyService) {
        this.applicationPropertyService = applicationPropertyService;
    }

    @Autowired
    public void setCodeTableDao(CodeTableDao codeTableDao) {
        this.codeTableDao = codeTableDao;
    }

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void updateSourceClassificationCodes(Date lastPullDate) {

        Map<String, DataCategory> dataCategoryMap = null;
        Map<String, SectorCode> sectorCodeMap = null;
        StringBuilder exceptionBuffer = new StringBuilder();

        //load up needed maps and other information
        //load sub model objects to be resolved -- DataCategory, Sector
        dataCategoryMap = getDataCategoryMap();
        sectorCodeMap = getSectorCodeMap(false);


        //Begin Save Sector Model Objects

        //see if any new sectors were added...
        Boolean addedSector = false;

        for (SectorCodeAdapter sectorCodeAdapter : getSectorCodes()) {
            String sectorCodeAdapterCode = sectorCodeAdapter.getAttributes().getSectorCode().getText();
            Date sectorCodeAdapterLastUpdated = sectorCodeAdapter.getLastUpdated();
            Date sectorCodeLastUpdated = sectorCodeMap.get(sectorCodeAdapterCode) != null ? sectorCodeMap.get(sectorCodeAdapterCode).getLastUpdatedDate() : null;
            //LOG.info(sectorCodeAdapterCode + "," + sectorCodeAdapter.getUid() + ",\"" + sectorCodeAdapter.getCode() + "\"");
            //add new sector
            SectorCode sectorCode = new SectorCode();
            sectorCodeAdapter.prepareForSave(sectorCode);

            //insert new sector
            if (!sectorCodeMap.containsKey(sectorCodeAdapterCode)) {
                try {
                    codeTableDao.insert(sectorCode);
                    codeTableDao.flushClear();
                    addedSector = true;
                } catch (Exception e) {
                    addException(e, exceptionBuffer);
                    LOG.info("Issue with saving Sector: " + sectorCodeAdapter.getCode(), e);
                }

            //update existing sector, if last updated date changed, use epoch time.
            } else if (sectorCodeMap.containsKey(sectorCodeAdapterCode)
                        && (sectorCodeLastUpdated == null
                        || !(sectorCodeLastUpdated.getTime() == sectorCodeAdapterLastUpdated.getTime())
            )) {
                try {
                    codeTableDao.update(sectorCode);
                    codeTableDao.flushClear();
                    addedSector = true;
                } catch (Exception e) {
                    addException(e, exceptionBuffer);
                    LOG.info("Issue with saving Sector: " + sectorCodeAdapter.getCode(), e);
                }
            }

        }

        //load up new sector map, using External Id as key
        sectorCodeMap = getSectorCodeMap(true);

        //End Save Sector Model Objects

        //Begin Save Source Classification Code Model Objects

        for (SourceClassificationCodeAdapter sourceClassificationCodeAdapter : getSourceClassificationCodes(lastPullDate)) {

            Date sourceClassificationCodeAdapterLastUpdated = sourceClassificationCodeAdapter.getLastUpdated();
            Boolean sourceClassifcationCodeExists = codeTableDao.codeExists(SourceClassificationCode.class, sourceClassificationCodeAdapter.getCode());
            SourceClassificationCode existingSourceClassificationCode = sourceClassifcationCodeExists ? codeTableDao.findSourceClassificationCode(sourceClassificationCodeAdapter.getCode()) : null;
            Date sourceClassifcationCodeLastUpdated = sourceClassifcationCodeExists ? existingSourceClassificationCode.getLastUpdatedDate() : null;

            SourceClassificationCode sourceClassificationCode = new SourceClassificationCode();
            sourceClassificationCodeAdapter.prepareForSave(sourceClassificationCode, dataCategoryMap, sectorCodeMap);

            //update existing scc, if last updated date changed, use epoch time.
            if (sourceClassifcationCodeExists
                    && (sourceClassifcationCodeLastUpdated == null
                    || !(sourceClassifcationCodeLastUpdated.getTime() == sourceClassificationCodeAdapterLastUpdated.getTime())
                    )) {
                try {
                    codeTableDao.update(sourceClassificationCode);
                    codeTableDao.flushClear();
                } catch (Exception e) {
                    addException(e, exceptionBuffer);
                    LOG.info("Issue with saving SCC: " + sourceClassificationCodeAdapter.getCode(), e);
                }

            //insert new scc
            } else if (!sourceClassifcationCodeExists) {
                try {
                    codeTableDao.insert(sourceClassificationCode);
                    codeTableDao.flushClear();
                } catch (Exception e) {
                    addException(e, exceptionBuffer);
                    LOG.info("Issue with saving SCC: " + sourceClassificationCodeAdapter.getCode(), e);
                }

            }

        }

        //End Save Source Classification Code Model Objects

        //check for exceptions, if some then email EIS support staff...
        if (exceptionBuffer.length() > 0)
            sendMail(exceptionBuffer.toString());
    }

    private void addException(final Exception e, final StringBuilder exceptionBuffer) {
        // Print stack trace to string
        final Writer stackTrace = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stackTrace);
        e.printStackTrace(printWriter);

        exceptionBuffer.append(stackTrace.toString());
        exceptionBuffer.append('\n');

    }

    private void sendMail(final String msgText) {
        // applicationPropertyService throws an exception if the property does not exist.
        final String mailTo =  applicationPropertyService.get("synaptica.sccpull.mail.exception.to").getValue();    //"Miller.Jonathan@epa.gov;DelVecchio.Darin@epa.gov;VictoriaAWasem@maximus.com;PaulCooper@Maximus.com";
        final String mailSubject = applicationPropertyService.get("synaptica.sccpull.mail.exception.subject").getValue();
        final String mailExceptionFrom = applicationPropertyService.get("mail.exception.from").getValue();

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mailTo.replace(" ","").split(";"));
        msg.setFrom(mailExceptionFrom);
        msg.setSubject(mailSubject + " - Batch SCC Pull Web Service");
        msg.setText(msgText);

        mailService.sendMessage(msg);
    }


    //keyed on DataCategory.desc
    private Map<String, DataCategory> getDataCategoryMap() {
        Map<String, DataCategory> dataCategoryMap = new HashMap<String, DataCategory>();
        for (DataCategory dataCategory : codeTableDao.getAllDataCategories()) {
            dataCategoryMap.put(dataCategory.getDescription(), dataCategory);
        }
        return dataCategoryMap;
    }

    //keyed on sector code
    private Map<String, SectorCode> getSectorCodeMap(boolean useExternalId) {
        Map<String, SectorCode> sectorCodeMap = new HashMap<String, SectorCode>();
        for (SectorCode sectorCode : codeTableDao.getAllSectorCodes()) {
            sectorCodeMap.put(!useExternalId ? sectorCode.getCode() : sectorCode.getExternalId(), sectorCode);
        }
        return sectorCodeMap;
    }

    @Override
    public SourceClassificationCodeAdapter[] getSourceClassificationCodes(Date lastUpdatedDate) {
        FacetFilter facetFilter = new FacetFilter();
//        List<Facet> facets = new ArrayList<Facet>();
//        facets.add(new Facet(FacetFilter.Field.LAST_UPDATED_DT.getFieldName(), dateFormat.format(lastUpdatedDate)));
//        facetFilter.setFacets(facets);
//        facetFilter.setMatchType(Facet.MatchType.whole_phrase);
//        facetFilter.setQualifier(Facet.Qualifier.begins);
        facetFilter.setLastUpdatedSince(dateFormat.format(lastUpdatedDate));
        facetFilter.setSortFacet(FacetFilter.Field.CODE.getFieldName());

        return sourceClassificationCodeWebService.getSourceClassificationCodes(facetFilter);
    }

    @Override
    public SectorCodeAdapter[] getSectorCodes() {
        return sourceClassificationCodeWebService.getSectorCodes();
    }
}
