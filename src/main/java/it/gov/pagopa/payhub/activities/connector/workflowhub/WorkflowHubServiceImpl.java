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
    private final WorkflowHubClient debtPositionClient;

    public WorkflowHubServiceImpl(AuthnService authnService, WorkflowHubClient debtPositionClient) {
        this.authnService = authnService;
        this.debtPositionClient = debtPositionClient;
    }

    @Override
    public WorkflowStatusDTO getWorkflowStatus(String workflowId) {
        String accessToken = authnService.getAccessToken();
        return debtPositionClient.getWorkflowStatus(accessToken, workflowId);
    }
}
