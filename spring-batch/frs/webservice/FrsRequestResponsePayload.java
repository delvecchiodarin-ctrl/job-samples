package gov.epa.eis.batch.frs.webservice;

import gov.epa.eis.model.webservice.WebServiceTransaction;
import org.apache.commons.io.FileUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by DDelVecc on 5/9/2019.
 */
public class FrsRequestResponsePayload {
    private String uri;
    private String method;
    private String requestHeaders;
    private byte[] requestBody;
    private int statusCode;
    private String statusText;
    private String responseHeaders;
    private String responseBody;

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setResponseHeaders(String responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getUri() {
        return uri;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void load(final WebServiceTransaction webServiceTransaction) {
        webServiceTransaction.setRequestUri(getUri());
        webServiceTransaction.setHttpMethod(getMethod());
        try {
            webServiceTransaction.setRequestBody(new String(getRequestBody(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webServiceTransaction.setRequestHeaders(getRequestHeaders());
        webServiceTransaction.setResponseHeaders(getResponseHeaders());
        webServiceTransaction.setResponseBody(getResponseBody());
        webServiceTransaction.setStatusCode(getStatusCode());
        webServiceTransaction.setStatusText(getStatusText());
    }
}
