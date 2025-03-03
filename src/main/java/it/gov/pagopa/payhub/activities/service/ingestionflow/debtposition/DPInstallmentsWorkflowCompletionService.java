package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.TooManyAttemptsException;
import it.gov.pagopa.payhub.activities.service.WorkflowCompletionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.temporal.api.enums.v1.WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED;

@Service
@Lazy
@Slf4j
public class DPInstallmentsWorkflowCompletionService {

    private final WorkflowCompletionService workflowCompletionService;
    private final int maxAttempts;
    private final int retryDelayMs;

    public DPInstallmentsWorkflowCompletionService(WorkflowCompletionService workflowCompletionService,
                                                   @Value("${ingestion-flow-files.dp-installments.wf-await.max-waiting-minutes:5}") int maxWaitingMinutes,
                                                   @Value("${ingestion-flow-files.dp-installments.wf-await.retry-delays-ms:1000}") int retryDelayMs) {
        this.workflowCompletionService = workflowCompletionService;
        this.retryDelayMs = retryDelayMs;
        this.maxAttempts = (int) (((double) maxWaitingMinutes * 60_000) / retryDelayMs);
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
    public boolean waitForWorkflowCompletion(String workflowId, InstallmentIngestionFlowFileDTO installment, Long ingestionFlowFileLineNumber,
                                             String fileName, List<InstallmentErrorDTO> errorList) {
        try {
            if (workflowId == null) {
                return true;
            }
            WorkflowExecutionStatus workflowStatus = workflowCompletionService.waitTerminationStatus(
                    workflowId, maxAttempts, retryDelayMs);

            if (!WORKFLOW_EXECUTION_STATUS_COMPLETED.equals(workflowStatus)) {
                errorList.add(buildInstallmentErrorDTO(fileName, installment, ingestionFlowFileLineNumber, workflowStatus.name(),
                        "WORKFLOW_TERMINATED_WITH_FAILURE", "Workflow terminated with error status"));
                return false;
            }

            return true;
        } catch (TooManyAttemptsException e) {
            log.warn("Workflow {} did not complete within retry limits.", workflowId);
            errorList.add(buildInstallmentErrorDTO(fileName, installment, ingestionFlowFileLineNumber, null, "RETRY_LIMIT_REACHED", "Maximum number of retries reached"));
            return false;
        }
    }

    private InstallmentErrorDTO buildInstallmentErrorDTO(String fileName, InstallmentIngestionFlowFileDTO installment, Long ingestionFlowFileLineNumber,
                                                         String workflowStatus, String errorCode, String errorMessage) {
        return InstallmentErrorDTO.builder()
                .fileName(fileName)
                .iupdOrg(installment.getIupdOrg())
                .iud(installment.getIud())
                .workflowStatus(workflowStatus)
                .rowNumber(ingestionFlowFileLineNumber)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
