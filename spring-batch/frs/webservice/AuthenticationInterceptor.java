package gov.epa.eis.batch.frs.webservice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Created by DDelVecc on 5/9/2019.
 */
public class AuthenticationInterceptor implements ClientHttpRequestInterceptor {
    private final String userId;
    private final String password;

    public AuthenticationInterceptor(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution ) throws IOException {
        HttpHeaders httpHeaders = request.getHeaders();
        httpHeaders.set("UserId", userId);
        httpHeaders.set("Password",password);
        return execution.execute(request, body);
    }
}