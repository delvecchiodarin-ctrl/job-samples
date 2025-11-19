package gov.epa.eis.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

@Transactional
public class RestWebServiceClient extends WebServiceGatewaySupport {

    private RestTemplate restTemplate;
    private String webServiceEndPointUri;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T getResponse(String uri, Class<T> clazz) {
        return restTemplate.getForObject(uri, clazz);
    }

    public static String buildFacetSearchQueryString(FacetFilter facetFilter) {
        if (facetFilter == null) return "";

        String qs = "";
        String nameQS = "";
        String valueQS = "";
        if (facetFilter.getFacets() != null) {
            for (Facet facet : facetFilter.getFacets()) {
                    nameQS += (nameQS.length() > 0 ? "|" : "")
                            + facet.getName();
                    valueQS += (valueQS.length() > 0 ? "|" : "")
                            + facet.getValue();
            }
            qs += (qs.length() > 0 ? "&" : "")
                    + "facetName[]=" + nameQS + "&facetValue[]=" + valueQS;
            if (facetFilter.getQualifier() != null)
                qs += "&facetQualifier[]=" + facetFilter.getQualifier().toString();
            if (facetFilter.getMatchType() != null)
                qs += "&facetMatchType[]=" + facetFilter.getMatchType().toString();
        }
        if (facetFilter.getPageSize() != null && facetFilter.getPageSize() > 0)
            qs += "&pageSize=" + facetFilter.getPageSize();
        if (facetFilter.getPageNum() != null && facetFilter.getPageNum() > 0)
            qs += "&pageNum=" + facetFilter.getPageNum();
        if (facetFilter.getSortFacet() != null && facetFilter.getSortFacet().length() > 0)
            qs += "&sortFacet=" + facetFilter.getSortFacet();
        if (facetFilter.getLastUpdatedSince() != null && facetFilter.getLastUpdatedSince().length() > 0)
            qs += "&lastUpdatedSince=" + facetFilter.getLastUpdatedSince();
        return qs;
    }

    public String getWebServiceEndPointUri() {
        return webServiceEndPointUri;
    }

    public void setWebServiceEndPointUri(String webServiceEndPointUri) {
        this.webServiceEndPointUri = webServiceEndPointUri;
    }
}
