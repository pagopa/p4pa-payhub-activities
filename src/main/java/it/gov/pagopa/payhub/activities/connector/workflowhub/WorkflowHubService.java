package it.gov.pagopa.payhub.activities.connector.workflowhub;

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

}
