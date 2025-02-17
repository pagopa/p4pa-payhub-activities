package it.gov.pagopa.payhub.activities.connector.workflowhub.config;


import it.gov.pagopa.pu.workflowhub.controller.generated.WorkflowApi;
import it.gov.pagopa.pu.workflowhub.generated.ApiClient;
import it.gov.pagopa.pu.workflowhub.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class WorkflowHubApisHolder {
    private final WorkflowApi workflowApi;
    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public WorkflowHubApisHolder(
	    @Value("${rest.workflow-hub.base-url}") String baseUrl,
	    RestTemplateBuilder restTemplateBuilder) {
	    RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(bearerTokenHolder::get);

        this.workflowApi = new WorkflowApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link WorkflowApi} instrumented with the provided accessToken. Use null if auth is not required */
    public WorkflowApi getWorkflowHubApi(String accessToken){
        return getApi(accessToken, workflowApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
