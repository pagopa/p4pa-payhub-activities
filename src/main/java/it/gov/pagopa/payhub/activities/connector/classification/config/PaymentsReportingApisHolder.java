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
public class PaymentsReportingApisHolder {

    private final PaymentsReportingSearchControllerApi paymentsReportingSearchControllerApi;
    private final PaymentsReportingEntityControllerApi paymentsReportingEntityControllerApi;
    private final PaymentsReportingEntityExtendedControllerApi paymentsReportingEntityExtendedControllerApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public PaymentsReportingApisHolder(
            @Value("${rest.classification.base-url}") String baseUrl,
            RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(bearerTokenHolder::get);

        this.paymentsReportingSearchControllerApi = new PaymentsReportingSearchControllerApi(apiClient);
        this.paymentsReportingEntityControllerApi = new PaymentsReportingEntityControllerApi(apiClient);
        this.paymentsReportingEntityExtendedControllerApi = new PaymentsReportingEntityExtendedControllerApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link PaymentsReportingSearchControllerApi} instrumented with the provided accessToken.*/
    public PaymentsReportingSearchControllerApi getPaymentsReportingSearchApi(String accessToken){
        return getApi(accessToken, paymentsReportingSearchControllerApi);
    }

    public PaymentsReportingEntityControllerApi getPaymentsReportingEntityControllerApi(String accessToken){
        return getApi(accessToken, paymentsReportingEntityControllerApi);
    }
    public PaymentsReportingEntityExtendedControllerApi getPaymentsReportingEntityExtendedControllerApi(String accessToken){
        return getApi(accessToken, paymentsReportingEntityExtendedControllerApi);
    }



    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
