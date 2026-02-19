package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static io.temporal.api.enums.v1.WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED;

@Service
@Lazy
@Slf4j
public class DPInstallmentsWorkflowCompletionService {

    private final WorkflowHubService workflowHubService;
    private final int maxAttempts;
    private final int retryDelayMs;

    public DPInstallmentsWorkflowCompletionService(WorkflowHubService workflowHubService,
                                                   @Value("${ingestion-flow-files.dp-installments.wf-await.max-waiting-minutes}") int maxWaitingMinutes,
                                                   @Value("${ingestion-flow-files.dp-installments.wf-await.retry-delays-ms}") int retryDelayMs) {
        this.workflowHubService = workflowHubService;
        this.retryDelayMs = retryDelayMs;
        this.maxAttempts = (int) (((double) maxWaitingMinutes * 60_000) / retryDelayMs);
    }

    /**
     * Waits for a workflow to reach a terminal status.
     *
     * @param workflowId  The ID of the workflow to monitor.
     * @param installment The installment ingestion flow file DTO associated with the workflow.
     * @return {@code true} if the workflow completed successfully, {@code false} if it terminated with an error or exceeded the retry limit.
     */
    public List<InstallmentErrorDTO> waitForWorkflowCompletion(String workflowId, InstallmentIngestionFlowFileDTO installment,
                                                               Long ingestionFlowFileLineNumber) {
        try {
            if (workflowId == null) {
                return Collections.emptyList();
            }
            WorkflowExecutionStatus workflowStatus = workflowHubService.waitWorkflowCompletion(
                    workflowId, maxAttempts, retryDelayMs).getStatus();

            if (!WORKFLOW_EXECUTION_STATUS_COMPLETED.equals(workflowStatus)) {
                return List.of(buildInstallmentErrorDTO(installment,
                        ingestionFlowFileLineNumber,
                        FileErrorCode.WORKFLOW_TERMINATED_WITH_FAILURE.name(),
                        FileErrorCode.WORKFLOW_TERMINATED_WITH_FAILURE.getMessage()));
            }

            return Collections.emptyList();
        } catch (Exception e) {
            log.warn("Workflow {} did not complete within retry limits.", workflowId);
            return List.of(buildInstallmentErrorDTO(installment,
                    ingestionFlowFileLineNumber,
                    FileErrorCode.WORKFLOW_TIMEOUT.name(),
                    FileErrorCode.WORKFLOW_TIMEOUT.getMessage()));
        }
    }

    private InstallmentErrorDTO buildInstallmentErrorDTO(InstallmentIngestionFlowFileDTO installment,
                                                         Long ingestionFlowFileLineNumber,
                                                         String errorCode, String errorMessage) {
        return InstallmentErrorDTO.builder()
                .csvRow(installment != null ? installment.getRow(): null)
                .rowNumber(ingestionFlowFileLineNumber)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
