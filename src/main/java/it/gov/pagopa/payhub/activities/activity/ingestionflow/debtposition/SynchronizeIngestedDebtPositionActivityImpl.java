package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowDebtPositionService;
import it.gov.pagopa.payhub.activities.service.WorkflowCompletionService;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedDebtPositions;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.temporal.api.enums.v1.WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED;

@Slf4j
@Lazy
@Service
public class SynchronizeIngestedDebtPositionActivityImpl implements SynchronizeIngestedDebtPositionActivity {

    private final DebtPositionService debtPositionService;
    private final WorkflowDebtPositionService workflowDebtPositionService;
    private final WorkflowCompletionService workflowCompletionService;
    private final Integer pageSize;
    private final int maxAttempts;
    private final int retryDelayMs;

    private static final List<String> DEFAULT_ORDERING = List.of("debtPositionId,asc");

    public SynchronizeIngestedDebtPositionActivityImpl(DebtPositionService debtPositionService, WorkflowDebtPositionService workflowDebtPositionService, WorkflowCompletionService workflowCompletionService,
                                                       @Value("${query-limits.debt-positions.size}") Integer pageSize,
                                                       @Value("${ingestion-flow-files.dp-installments.wf-await.max-waiting-minutes:5}") int maxWaitingMinutes,
                                                       @Value("${ingestion-flow-files.dp-installments.wf-await.retry-delays-ms:1000}") int retryDelayMs) {
        this.debtPositionService = debtPositionService;
        this.workflowDebtPositionService = workflowDebtPositionService;
        this.workflowCompletionService = workflowCompletionService;
        this.pageSize = pageSize;
        this.maxAttempts = (int) (((double) maxWaitingMinutes * 60_000) / retryDelayMs);
        this.retryDelayMs = retryDelayMs;
    }

    @Override
    public String synchronizeIngestedDebtPosition(Long ingestionFlowFileId) {
        log.info("Synchronizing all debt positions related to ingestion flow file id {}", ingestionFlowFileId);

        StringBuilder errors = new StringBuilder();

        int currentPage = 0;
        boolean hasMorePages = true;

        while (hasMorePages) {
            PagedDebtPositions pagedDebtPositions = debtPositionService.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId,
                    currentPage,
                    pageSize,
                    DEFAULT_ORDERING);

            log.info("Synchronizing page {} of {} retrieved searching debt positions related to ingestionFlowFileId {} (totalElements {})",
                    currentPage, pagedDebtPositions.getTotalPages(), pagedDebtPositions.getTotalElements(), ingestionFlowFileId);

            pagedDebtPositions.getContent().forEach(debtPosition -> {
                try {
                    PaymentEventType paymentEventType = null; //TODO task P4ADEV-2421
                    WorkflowCreatedDTO workflowCreatedDTO = workflowDebtPositionService.syncDebtPosition(debtPosition, false, paymentEventType);

                    if(workflowCreatedDTO == null || workflowCreatedDTO.getWorkflowId() == null){
                        errors.append("No synchronization workflow created for debt position with iupdOrg " + debtPosition.getIupdOrg() + "\n");
                        return;
                    }

                    WorkflowExecutionStatus workflowExecutionStatus = workflowCompletionService.waitTerminationStatus(workflowCreatedDTO.getWorkflowId(), maxAttempts, retryDelayMs);

                    if (!WORKFLOW_EXECUTION_STATUS_COMPLETED.equals(workflowExecutionStatus)) {
                        errors.append("Synchronization workflow for debt position with iupdOrg " + debtPosition.getIupdOrg() + " terminated with error status.\n");
                    }

                } catch (Exception e) {
                    log.error("Error synchronizing debt position with id {} and iupdOrg {}: {}", debtPosition.getDebtPositionId(), debtPosition.getIupdOrg(), e.getMessage());
                    errors.append("Error on debt position with iupdOrg " + debtPosition.getIupdOrg() + ": " + e.getMessage() + " \n");
                }
            });

            currentPage++;
            hasMorePages = currentPage < pagedDebtPositions.getTotalPages();
        }

        log.info("Synchronization of all debt positions related to ingestion flow file id {} completed", ingestionFlowFileId);
        return errors.toString();
    }
}
