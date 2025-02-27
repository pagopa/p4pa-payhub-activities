package it.gov.pagopa.payhub.activities.service;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.TooManyAttemptsException;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Set;

import static io.temporal.api.enums.v1.WorkflowExecutionStatus.*;


@Lazy
@Slf4j
@Service
public class WorkflowCompletionService {

    private final WorkflowHubService workflowHubService;

    /** <a href="https://docs.temporal.io/workflows#status">Closed statuses</a> */
    private final Set<WorkflowExecutionStatus> wfTerminationStatuses = Set.of(
            WORKFLOW_EXECUTION_STATUS_FAILED,
            WORKFLOW_EXECUTION_STATUS_TERMINATED,
            WORKFLOW_EXECUTION_STATUS_CANCELED,
            WORKFLOW_EXECUTION_STATUS_TIMED_OUT,
            WORKFLOW_EXECUTION_STATUS_COMPLETED
    );

    public WorkflowCompletionService(WorkflowHubService workflowHubService) {
        this.workflowHubService = workflowHubService;
    }

    /**
     * Waits for a workflow to reach a terminal status.
     *
     * @param workflowId   The ID of the workflow to monitor.
     * @param maxAttempts   The maximum number of retry attempts.
     * @param retryDelayMs The delay in milliseconds between retries.
     * @return The final {@link WorkflowExecutionStatus}.
     * @throws TooManyAttemptsException If the retry limit is exceeded.
     */
    public WorkflowExecutionStatus waitTerminationStatus(String workflowId, int maxAttempts, int retryDelayMs) throws TooManyAttemptsException {

        maxAttempts = Math.max(maxAttempts, 1);
        int attempts = 0;
        WorkflowExecutionStatus workflowStatus;

        do {
            WorkflowStatusDTO statusDTO = workflowHubService.getWorkflowStatus(workflowId);
            log.debug("Retrieved workflow status: {}", statusDTO);
            workflowStatus = transcodeStatus(statusDTO.getStatus());

            if (workflowStatus != null && wfTerminationStatuses.contains(workflowStatus)) {
                return workflowStatus;
            }

            attempts++;
            try {
                Thread.sleep(retryDelayMs);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while waiting for workflow completion. Attempt {}/{}", attempts, maxAttempts);
            }
        } while (attempts <= maxAttempts);

        log.info("Workflow {} did not complete after {} retries. No further attempts will be made.", workflowId, maxAttempts);
        throw new TooManyAttemptsException("Maximum number of retries reached for workflow " + workflowId);
    }

    private WorkflowExecutionStatus transcodeStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return WorkflowExecutionStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            log.error("Unknown workflow status received: {}", status);
            return null;
        }
    }
}
