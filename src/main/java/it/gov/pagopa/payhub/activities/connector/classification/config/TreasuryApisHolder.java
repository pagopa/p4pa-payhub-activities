package it.gov.pagopa.payhub.activities.connector.classification.config;

import it.gov.pagopa.pu.classification.client.generated.TreasuryEntityControllerApi;
import it.gov.pagopa.pu.classification.client.generated.TreasuryEntityExtendedControllerApi;
import it.gov.pagopa.pu.classification.client.generated.TreasurySearchControllerApi;
import it.gov.pagopa.pu.classification.generated.ApiClient;
import it.gov.pagopa.pu.classification.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class TreasuryApisHolder {

    private final TreasurySearchControllerApi treasurySearchControllerApi;

    private final TreasuryEntityControllerApi treasuryEntityControllerApi;
    private final TreasuryEntityExtendedControllerApi treasuryEntityExtendedControllerApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public TreasuryApisHolder(
            @Value("${rest.classification.base-url}") String baseUrl,
            RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(bearerTokenHolder::get);

        this.treasurySearchControllerApi = new TreasurySearchControllerApi(apiClient);
        this.treasuryEntityControllerApi = new TreasuryEntityControllerApi(apiClient);
        this.treasuryEntityExtendedControllerApi = new TreasuryEntityExtendedControllerApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link TreasurySearchControllerApi} instrumented with the provided accessToken.*/
    public TreasurySearchControllerApi getTreasurySearchApi(String accessToken){
        return getApi(accessToken, treasurySearchControllerApi);
    }

    public TreasuryEntityControllerApi getTreasuryEntityControllerApi(String accessToken){
        return getApi(accessToken, treasuryEntityControllerApi);
    }
    public TreasuryEntityExtendedControllerApi getTreasuryEntityExtendedControllerApi(String accessToken){
        return getApi(accessToken, treasuryEntityExtendedControllerApi);
    }



    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
