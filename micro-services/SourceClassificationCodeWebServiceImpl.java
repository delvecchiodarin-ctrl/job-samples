package gov.epa.eis.service.impl;

import gov.epa.eis.model.adapter.SectorCodeAdapter;
import gov.epa.eis.model.adapter.SourceClassificationCodeAdapter;
import gov.epa.eis.model.resource.ApplicationProperty;
import gov.epa.eis.service.ApplicationPropertyService;
import gov.epa.eis.service.SourceClassificationCodeWebService;
import gov.epa.eis.webservice.FacetFilter;
import gov.epa.eis.webservice.RestWebServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * SourceClassificationCodeWebServiceImpl
 */
@Transactional
public final class SourceClassificationCodeWebServiceImpl implements SourceClassificationCodeWebService {
    private static final Log LOG = LogFactory.getLog(SourceClassificationCodeWebServiceImpl.class);
    private RestWebServiceClient restWebServiceClient;
    private ApplicationPropertyService applicationPropertyService;
    private String webServiceEndPointUri;

    @Autowired
    public void setRestWebServiceClient(final RestWebServiceClient restWebServiceClient) {
        this.restWebServiceClient = restWebServiceClient;
    }

    @Autowired
    public void setApplicationPropertyService(final ApplicationPropertyService applicationPropertyService) {
        this.applicationPropertyService = applicationPropertyService;
    }

    @Override
    public SourceClassificationCodeAdapter[] getAllSourceClassificationCode() {
        return restWebServiceClient.getResponse(getWebServiceEndPointUri() + "/SCC?" + RestWebServiceClient.buildFacetSearchQueryString(new FacetFilter()), SourceClassificationCodeAdapter[].class);
    }

    @Override
    public SourceClassificationCodeAdapter[] getSourceClassificationCodes(FacetFilter facetFilter) {
        return restWebServiceClient.getResponse(getWebServiceEndPointUri() + "/SCC?" + RestWebServiceClient.buildFacetSearchQueryString(facetFilter), SourceClassificationCodeAdapter[].class);
    }

    @Override
    public String getSourceClassificationCodesInJSON(FacetFilter facetFilter) {
        return restWebServiceClient.getResponse(getWebServiceEndPointUri() + "/SCC?" + RestWebServiceClient.buildFacetSearchQueryString(facetFilter), String.class);
    }

    @Override
    public SourceClassificationCodeAdapter getSourceClassificationCode(String code) {
        return restWebServiceClient.getResponse(getWebServiceEndPointUri() + "/SCC/" + code, SourceClassificationCodeAdapter.class);
    }

    @Override
    public SectorCodeAdapter[] getSectorCodes() {
        return restWebServiceClient.getResponse(getWebServiceEndPointUri() + "/LookupElement/1005802", SectorCodeAdapter[].class);
    }

    @Override
    public String getWebServiceEndPointUri() {
        if (StringUtils.isBlank(webServiceEndPointUri)) {
            ApplicationProperty applicationProperty = applicationPropertyService.get(ApplicationProperty.Key.SCC_WEB_SERVICE_END_POINT_URI.getKeyName());
            webServiceEndPointUri = (applicationProperty != null ? applicationProperty.getValue() : "");
        }
        return webServiceEndPointUri;
    }
}
