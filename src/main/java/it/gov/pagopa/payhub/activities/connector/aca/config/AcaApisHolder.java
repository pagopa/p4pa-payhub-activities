package it.gov.pagopa.payhub.activities.connector.aca.config;

import it.gov.pagopa.pu.pagopapayments.client.generated.AcaApi;
import it.gov.pagopa.pu.pagopapayments.generated.ApiClient;
import it.gov.pagopa.pu.pagopapayments.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class AcaApisHolder {

    private final AcaApi acaApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public AcaApisHolder(
            @Value("${rest.io-notification.base-url}") String baseUrl,
            RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(bearerTokenHolder::get);

        this.acaApi = new AcaApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link AcaApi} instrumented with the provided accessToken. Use null if auth is not required */
    public AcaApi getAcaApi(String accessToken){
        return getApi(accessToken, acaApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
