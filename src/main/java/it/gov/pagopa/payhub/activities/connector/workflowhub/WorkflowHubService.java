package it.gov.pagopa.payhub.activities.connector.workflowhub;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;

/**
 * This interface provides methods to observe a workflow.
 */
public interface WorkflowHubService {

    /**
     * Retrieve the workflow status
     *
     * @param workflowId the identifier of the workflow.
     * @return {@link WorkflowStatusDTO} that contains the workflow status.
     */
    WorkflowStatusDTO getWorkflowStatus(String workflowId);

    /**
     * Waits for a Temporal workflow to reach a terminal status
     *
     * @param workflowId   the identifier of the workflow
     * @param maxAttempts  the maximum number of attempts before giving up
     * @param retryDelayMs the delay in milliseconds between each attempt
     * @return the final {@link WorkflowExecutionStatus} of the workflow
     */
    WorkflowExecutionStatus waitWorkflowCompletion(String workflowId, Integer maxAttempts, Integer retryDelayMs);

}
