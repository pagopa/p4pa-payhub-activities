package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static io.temporal.api.enums.v1.WorkflowExecutionStatus.*;

@Service
@Lazy
@Slf4j
public class WorkflowCompletionService {

    private final WorkflowHubService workflowHubService;
    private final int maxRetries;
    private final int retryDelayMs;

    private final Set<WorkflowExecutionStatus> TERMINAL_STATUSES = Set.of(
            WORKFLOW_EXECUTION_STATUS_FAILED,
            WORKFLOW_EXECUTION_STATUS_TERMINATED,
            WORKFLOW_EXECUTION_STATUS_CANCELED,
            WORKFLOW_EXECUTION_STATUS_TIMED_OUT,
            WORKFLOW_EXECUTION_STATUS_COMPLETED
    );

    public WorkflowCompletionService(WorkflowHubService workflowHubService,
                                     @Value("${ingestion-flow-files.dp-installments.wf-await.max-waiting-minutes:5}") double maxWaitingMinutes,
                                     @Value("${ingestion-flow-files.dp-installments.wf-await.retry-delays-ms:1000}") int retryDelayMs) {
        this.workflowHubService = workflowHubService;
        this.retryDelayMs = retryDelayMs;
        this.maxRetries = (int) ((maxWaitingMinutes * 60_000) / retryDelayMs);
    }

    /**
     * Waits for a workflow to reach a terminal status.
     *
     * @param workflowId  The ID of the workflow to monitor.
     * @param installment The installment ingestion flow file DTO associated with the workflow.
     * @param fileName    The name of the file being processed.
     * @param errorList   The list where errors encountered during processing will be recorded.
     * @return {@code true} if the workflow completed successfully, {@code false} if it terminated with an error or exceeded the retry limit.
     */
    public boolean waitForWorkflowCompletion(String workflowId, InstallmentIngestionFlowFileDTO installment,
                                             String fileName, List<InstallmentErrorDTO> errorList) {
        int attempts = 0;
        String status;

        do {
            WorkflowStatusDTO statusDTO = workflowHubService.getWorkflowStatus(workflowId);
            status = statusDTO.getStatus();
            WorkflowExecutionStatus workflowStatus = convertToWorkflowExecutionStatus(status);
            log.info("Workflow {} status: {}", workflowId, status);

            if (workflowStatus != null && TERMINAL_STATUSES.contains(workflowStatus)) {
                if (!WORKFLOW_EXECUTION_STATUS_COMPLETED.equals(workflowStatus)) {
                    errorList.add(buildInstallmentErrorDTO(fileName, installment, status, "WORKFLOW_TERMINATED_WITH_FAILURE", "Workflow terminated with error status"));
                    return false;
                }
                return true;
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
        errorList.add(buildInstallmentErrorDTO(fileName, installment, status, "RETRY_LIMIT_REACHED", "Maximum number of retries reached"));

        return false;
    }

    public InstallmentErrorDTO buildInstallmentErrorDTO(String fileName, InstallmentIngestionFlowFileDTO installment, String workflowStatus, String errorCode, String errorMessage) {
        return InstallmentErrorDTO.builder()
                .fileName(fileName)
                .iupdOrg(installment.getIupdOrg())
                .iud(installment.getIud())
                .workflowStatus(workflowStatus)
                .rowNumber(installment.getIngestionFlowFileLineNumber())
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
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
