package it.gov.pagopa.payhub.activities.connector.workflowhub.client;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.workflowhub.config.WorkflowHubApisHolder;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class WorkflowHubClient {

    private final WorkflowHubApisHolder workflowHubApisHolder;

    public WorkflowHubClient(WorkflowHubApisHolder workflowHubApisHolder) {
        this.workflowHubApisHolder = workflowHubApisHolder;
    }

    public WorkflowStatusDTO getWorkflowStatus(String accessToken, String workflowId){
        return workflowHubApisHolder.getWorkflowHubApi(accessToken).getWorkflowStatus(workflowId);
    }

    public WorkflowExecutionStatus waitWorkflowCompletion(String accessToken, String workflowId, Integer maxAttempts, Integer retryDelayMs){
        return workflowHubApisHolder.getWorkflowHubApi(accessToken).waitWorkflowCompletion(workflowId, maxAttempts, retryDelayMs).getStatus();
    }
}
