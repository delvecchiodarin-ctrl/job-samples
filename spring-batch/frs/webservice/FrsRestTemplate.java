package gov.epa.eis.batch.frs.webservice;

import gov.epa.eis.model.resource.ApplicationProperty;
import gov.epa.eis.service.ApplicationPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DDelVecc on 5/9/2019.
 */
@Component
public class FrsRestTemplate extends RestTemplate {
    private static String BASE_URL;
    public static String BULK_UPLOAD_URL;
    public static String BULK_UPLOAD_STATUS_URL;
    private ApplicationPropertyService applicationPropertyService;

    @Autowired
    public FrsRestTemplate(ApplicationPropertyService applicationPropertyService) {
        super(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        this.applicationPropertyService = applicationPropertyService;

        //get app properties
        //get web service properties; userid, password, endpoint uri

        this.BASE_URL = getApplicationPropertyValue(applicationPropertyService, ApplicationProperty.Key.FRSSUBMIT_WEBSERVICE_ENDPOINT_URI.getKeyName());
        this.BULK_UPLOAD_URL = getApplicationPropertyValue(applicationPropertyService, ApplicationProperty.Key.FRSSUBMIT_WEBSERVICE_BULKUPLOAD.getKeyName());
        this.BULK_UPLOAD_STATUS_URL = getApplicationPropertyValue(applicationPropertyService, ApplicationProperty.Key.FRSSUBMIT_WEBSERVICE_BULKUPLOAD_STATUS.getKeyName());

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new AuthenticationInterceptor(getApplicationPropertyValue(applicationPropertyService, ApplicationProperty.Key.FRSSUBMIT_WEBSERVICE_USERID.getKeyName()),
                getApplicationPropertyValue(applicationPropertyService, ApplicationProperty.Key.FRSSUBMIT_WEBSERVICE_PASSWORD.getKeyName())));
        interceptors.add(new RequestResponseLoggingInterceptor());
        this.setInterceptors(interceptors);
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables)
            throws RestClientException {
        URI uri = null;
        try {
            uri = new URI(BASE_URL + url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return super.postForEntity(uri, request, responseType);
    }

    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
                                          HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return super.exchange(BASE_URL + url, method,
                requestEntity, responseType,
                uriVariables);

    }

    private String getApplicationPropertyValue(final ApplicationPropertyService applicationPropertyService, final String key) {
        return applicationPropertyService.get(key).getValue();
    }
}
