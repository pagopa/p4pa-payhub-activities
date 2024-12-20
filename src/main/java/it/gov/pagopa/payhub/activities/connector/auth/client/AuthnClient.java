package it.gov.pagopa.payhub.activities.connector.auth.client;

import it.gov.pagopa.pu.p4paauth.controller.generated.AuthnApi;
import it.gov.pagopa.pu.p4paauth.dto.generated.AccessToken;
import it.gov.pagopa.pu.p4paauth.generated.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class AuthnClient {

    private final AuthnApi authnApi;

    public AuthnClient(
            @Value("${rest.auth.base-url}") String baseUrl,

            RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        authnApi = new AuthnApi(apiClient);
    }

    public AccessToken postToken(String clientId, String grantType, String scope, String subjectToken, String subjectIssuer, String subjectTokenType, String clientSecret) {
        return authnApi.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret);
    }
}
