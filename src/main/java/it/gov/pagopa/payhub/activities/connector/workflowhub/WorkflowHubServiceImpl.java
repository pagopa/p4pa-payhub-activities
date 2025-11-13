package it.gov.pagopa.payhub.activities.connector.workflowhub;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.client.WorkflowHubClient;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class WorkflowHubServiceImpl implements WorkflowHubService {

    private final AuthnService authnService;
    private final WorkflowHubClient workflowHubClient;

    public WorkflowHubServiceImpl(AuthnService authnService, WorkflowHubClient workflowHubClient) {
        this.authnService = authnService;
        this.workflowHubClient = workflowHubClient;
    }

    @Override
    public WorkflowStatusDTO getWorkflowStatus(String workflowId) {
        String accessToken = authnService.getAccessToken();
        return workflowHubClient.getWorkflowStatus(accessToken, workflowId);
    }

    @Override
    public WorkflowStatusDTO waitWorkflowCompletion(String workflowId, Integer maxAttempts, Integer retryDelayMs) {
        String accessToken = authnService.getAccessToken();
        return workflowHubClient.waitWorkflowCompletion(accessToken, workflowId, maxAttempts, retryDelayMs);
    }
}
