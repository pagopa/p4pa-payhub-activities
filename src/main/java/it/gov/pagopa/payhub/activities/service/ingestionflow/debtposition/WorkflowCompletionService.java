package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

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

    private final Set<WorkflowExecutionStatus> TERMINAL_STATUSES = Set.of(
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
     * @param maxRetries   The maximum number of retry attempts.
     * @param retryDelayMs The delay in milliseconds between retries.
     * @return The final {@link WorkflowExecutionStatus}.
     * @throws TooManyAttemptsException If the retry limit is exceeded.
     */
    public WorkflowExecutionStatus waitTerminationStatus(String workflowId, int maxRetries, int retryDelayMs) throws TooManyAttemptsException {

        int attempts = 0;
        WorkflowExecutionStatus workflowStatus;

        do {

            WorkflowStatusDTO statusDTO = workflowHubService.getWorkflowStatus(workflowId);
            String status = statusDTO.getStatus();
            workflowStatus = convertToWorkflowExecutionStatus(status);
            log.info("Workflow {} status: {}", workflowId, status);

            if (workflowStatus != null && TERMINAL_STATUSES.contains(workflowStatus)) {
                return workflowStatus;
            }

            attempts++;
            try {
                Thread.sleep(retryDelayMs);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Thread interrupted while waiting for workflow completion. Attempt {}/{}", attempts, maxRetries);
            }
        } while (attempts < maxRetries);

        log.warn("Workflow {} did not complete after {} retries. No further attempts will be made.", workflowId, maxRetries);
        throw new TooManyAttemptsException("Maximum number of retries reached for workflow " + workflowId);
    }

    private WorkflowExecutionStatus convertToWorkflowExecutionStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return WorkflowExecutionStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown workflow status received: {}", status);
            return null;
        }
    }
}
