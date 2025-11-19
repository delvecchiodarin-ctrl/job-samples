package gov.epa.eis.batch.frs.webservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by DDelVecc on 5/9/2019.
 */
public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Log LOG = LogFactory.getLog(RequestResponseLoggingInterceptor.class);

    public FrsRequestResponsePayload getFrsRequestResponsePayload() {
        return frsRequestResponsePayload;
    }

    private FrsRequestResponsePayload frsRequestResponsePayload;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        frsRequestResponsePayload = new FrsRequestResponsePayload();
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        frsRequestResponsePayload.setUri(request.getURI().toString());
        frsRequestResponsePayload.setMethod(request.getMethod().name());
        frsRequestResponsePayload.setRequestHeaders(request.getHeaders().toString());
        frsRequestResponsePayload.setRequestBody(body);
        if (LOG.isDebugEnabled()) {
            LOG.debug("===========================request begin================================================");
            LOG.debug(String.format("URI         : {%s}", request.getURI()));
            LOG.debug(String.format("Method      : {%s}", request.getMethod()));
            LOG.debug(String.format("Headers     : {%s}", request.getHeaders()));
//            LOG.debug(String.format("Request body: {%s}", new String(body, "UTF-8")));
            LOG.debug("==========================request end================================================");
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        frsRequestResponsePayload.setStatusCode(response.getStatusCode().value());
        frsRequestResponsePayload.setStatusText(response.getStatusText());
        frsRequestResponsePayload.setResponseHeaders(response.getHeaders().toString());
        frsRequestResponsePayload.setResponseBody(StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        if (LOG.isDebugEnabled()) {
            LOG.debug("============================response begin==========================================");
            LOG.debug(String.format("Status code  : {%s}", response.getStatusCode()));
            LOG.debug(String.format("Status text  : {%s}", response.getStatusText()));
            LOG.debug(String.format("Headers      : {%s}", response.getHeaders()));
            LOG.debug(String.format("Response body: {%s}", frsRequestResponsePayload.getResponseBody()));
            LOG.debug("=======================response end=================================================");
        }
    }

}