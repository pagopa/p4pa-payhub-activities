package it.gov.pagopa.payhub.activities.connector.classification.config;

import it.gov.pagopa.pu.classification.client.generated.*;
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
public class ClassificationApisHolder {

    private final ClassificationEntityControllerApi classificationEntityControllerApi;
    private final ClassificationEntityExtendedControllerApi classificationEntityExtendedControllerApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public ClassificationApisHolder(
            @Value("${rest.classification.base-url}") String baseUrl,
            RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(bearerTokenHolder::get);

        this.classificationEntityControllerApi = new ClassificationEntityControllerApi(apiClient);
        this.classificationEntityExtendedControllerApi = new ClassificationEntityExtendedControllerApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link TreasurySearchControllerApi} instrumented with the provided accessToken.*/

    public ClassificationEntityControllerApi getClassificationEntityControllerApi(String accessToken){
        return getApi(accessToken, classificationEntityControllerApi);
    }
    public ClassificationEntityExtendedControllerApi getClassificationEntityExtendedControllerApi(String accessToken){
        return getApi(accessToken, classificationEntityExtendedControllerApi);
    }



    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
