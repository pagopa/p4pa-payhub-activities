package it.gov.pagopa.payhub.activities.connector.processexecutions.config;

import it.gov.pagopa.pu.processexecutions.client.generated.IngestionFlowFileEntityControllerApi;
import it.gov.pagopa.pu.processexecutions.client.generated.IngestionFlowFileEntityExtendedControllerApi;
import it.gov.pagopa.pu.processexecutions.generated.ApiClient;
import it.gov.pagopa.pu.processexecutions.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class ProcessExecutionsApisHolder {

    private final IngestionFlowFileEntityControllerApi ingestionFlowFileEntityControllerApi;
    private final IngestionFlowFileEntityExtendedControllerApi ingestionFlowFileEntityExtendedControllerApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public IngestionFlowFileApisHolder(
            @Value("${rest.process-executions.base-url}") String baseUrl,
            RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(bearerTokenHolder::get);

        this.ingestionFlowFileEntityControllerApi = new IngestionFlowFileEntityControllerApi(apiClient);
        this.ingestionFlowFileEntityExtendedControllerApi = new IngestionFlowFileEntityExtendedControllerApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }


    public IngestionFlowFileEntityControllerApi getIngestionFlowFileEntityControllerApi(String accessToken){
        return getApi(accessToken, ingestionFlowFileEntityControllerApi);
    }
    public IngestionFlowFileEntityExtendedControllerApi getIngestionFlowFileEntityExtendedControllerApi(String accessToken){
        return getApi(accessToken, ingestionFlowFileEntityExtendedControllerApi);
    }



    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
