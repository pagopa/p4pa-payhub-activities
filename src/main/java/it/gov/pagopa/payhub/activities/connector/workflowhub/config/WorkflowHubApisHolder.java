package it.gov.pagopa.payhub.activities.connector.workflowhub.config;


import it.gov.pagopa.payhub.activities.config.rest.HttpClientErrorJsonBodyHandler;
import it.gov.pagopa.payhub.activities.connector.workflowhub.mapper.WorkflowErrorDTOMapper;
import it.gov.pagopa.pu.workflowhub.client.generated.DebtPositionApi;
import it.gov.pagopa.pu.workflowhub.client.generated.WorkflowApi;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowErrorDTO;
import it.gov.pagopa.pu.workflowhub.generated.ApiClient;
import it.gov.pagopa.pu.workflowhub.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

@Lazy
@Service
public class WorkflowHubApisHolder {

    private final WorkflowApi workflowApi;
    private final DebtPositionApi debtPositionApi;
    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public WorkflowHubApisHolder(
            WorkflowHubApiClientConfig clientConfig,
            RestTemplateBuilder restTemplateBuilder,
            JsonMapper jsonMapper
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        restTemplate.setErrorHandler(new HttpClientErrorJsonBodyHandler<>(jsonMapper, "WORKFLOW-HUB", clientConfig.isPrintBodyWhenError(),
                WorkflowErrorDTO.class, WorkflowErrorDTOMapper::map)
        );

        this.workflowApi = new WorkflowApi(apiClient);
        this.debtPositionApi = new DebtPositionApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link WorkflowApi} instrumented with the provided accessToken. Use null if auth is not required */
    public WorkflowApi getWorkflowHubApi(String accessToken){
        return getApi(accessToken, workflowApi);
    }

    /** It will return a {@link DebtPositionApi} instrumented with the provided accessToken. Use null if auth is not required */
    public DebtPositionApi getDebtPositionApi(String accessToken) {
        return getApi(accessToken, debtPositionApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
