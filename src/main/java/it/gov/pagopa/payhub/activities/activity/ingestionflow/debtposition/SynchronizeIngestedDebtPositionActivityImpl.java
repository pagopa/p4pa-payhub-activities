package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowDebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.SyncIngestedDebtPositionDTO;
import it.gov.pagopa.payhub.activities.service.WorkflowCompletionService;
import it.gov.pagopa.payhub.activities.service.debtposition.DebtPositionOperationTypeResolver;
import it.gov.pagopa.payhub.activities.service.pagopapayments.GenerateNoticeService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.temporal.api.enums.v1.WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED;

@Slf4j
@Lazy
@Service
public class SynchronizeIngestedDebtPositionActivityImpl implements SynchronizeIngestedDebtPositionActivity {

    private final DebtPositionService debtPositionService;
    private final WorkflowDebtPositionService workflowDebtPositionService;
    private final WorkflowCompletionService workflowCompletionService;
    private final GenerateNoticeService generateNoticeService;
    private final DebtPositionOperationTypeResolver debtPositionOperationTypeResolver;
    private final Integer pageSize;
    private final int maxAttempts;
    private final int retryDelayMs;

    private static final List<String> DEFAULT_ORDERING = List.of("debtPositionId,asc");

    public SynchronizeIngestedDebtPositionActivityImpl(DebtPositionService debtPositionService, WorkflowDebtPositionService workflowDebtPositionService, WorkflowCompletionService workflowCompletionService, GenerateNoticeService generateNoticeService, DebtPositionOperationTypeResolver debtPositionOperationTypeResolver,
                                                       @Value("${query-limits.debt-positions.size}") Integer pageSize,
                                                       @Value("${ingestion-flow-files.dp-installments.wf-await.max-waiting-minutes}") int maxWaitingMinutes,
                                                       @Value("${ingestion-flow-files.dp-installments.wf-await.retry-delays-ms}") int retryDelayMs) {
        this.debtPositionService = debtPositionService;
        this.workflowDebtPositionService = workflowDebtPositionService;
        this.workflowCompletionService = workflowCompletionService;
        this.generateNoticeService = generateNoticeService;
        this.debtPositionOperationTypeResolver = debtPositionOperationTypeResolver;
        this.pageSize = pageSize;
        this.maxAttempts = (int) (((double) maxWaitingMinutes * 60_000) / retryDelayMs);
        this.retryDelayMs = retryDelayMs;
    }

    @Override
    public SyncIngestedDebtPositionDTO synchronizeIngestedDebtPosition(Long ingestionFlowFileId) {
        log.info("Synchronizing all debt positions related to ingestion flow file id {}", ingestionFlowFileId);

        StringBuilder errors = new StringBuilder();

        int currentPage = 0;
        boolean hasMorePages = true;

        List<DebtPositionDTO> debtPositionsGenerateNotices = new ArrayList<>();

        while (hasMorePages) {
            PagedDebtPositions pagedDebtPositions = debtPositionService.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId,
                    currentPage,
                    pageSize,
                    DEFAULT_ORDERING);

            log.info("Synchronizing page {} of {} retrieved searching debt positions related to ingestionFlowFileId {} (totalElements {})",
                    currentPage, pagedDebtPositions.getTotalPages(), ingestionFlowFileId, pagedDebtPositions.getTotalElements());
            List<Pair<DebtPositionDTO, WorkflowCreatedDTO>> wfIds = syncDebtPositions(ingestionFlowFileId, pagedDebtPositions, errors);

            wfIds.forEach(p -> {
                WorkflowCreatedDTO workflow = p.getRight();
                DebtPositionDTO debtPosition = p.getLeft();
                try {
                    WorkflowExecutionStatus workflowExecutionStatus = workflowCompletionService.waitTerminationStatus(workflow.getWorkflowId(), maxAttempts, retryDelayMs);

                    if (!WORKFLOW_EXECUTION_STATUS_COMPLETED.equals(workflowExecutionStatus)) {
                        errors.append("\nSynchronization workflow for debt position with iupdOrg ")
                                .append(debtPosition.getIupdOrg())
                                .append(" terminated with error status.");
                    } else if (debtPosition.getFlagPuPagoPaPayment()) {
                        debtPositionsGenerateNotices.add(debtPosition);
                    }
                } catch (Exception e) {
                    log.error("Error waiting for debt position sync workflowId with id {} and iupdOrg {}: {}", workflow.getWorkflowId(), debtPosition.getIupdOrg(), e.getMessage());
                    errors.append("\nError on debt position with iupdOrg ")
                            .append(debtPosition.getIupdOrg()).append(": ")
                            .append(e.getMessage());
                }
            });

            currentPage++;
            hasMorePages = currentPage < pagedDebtPositions.getTotalPages();
        }

        log.info("Synchronization of all debt positions related to ingestion flow file id {} completed", ingestionFlowFileId);

        String pdfGeneratedId = null;
        if (!debtPositionsGenerateNotices.isEmpty()) {
            pdfGeneratedId = generateNoticeService.generateNotices(ingestionFlowFileId, debtPositionsGenerateNotices);
        }

        return SyncIngestedDebtPositionDTO.builder()
                .errorsDescription(errors.toString())
                .pdfGeneratedId(pdfGeneratedId)
                .build();
    }

    private List<Pair<DebtPositionDTO, WorkflowCreatedDTO>> syncDebtPositions(Long ingestionFlowFileId, PagedDebtPositions pagedDebtPositions, StringBuilder errors) {
        return pagedDebtPositions.getContent().stream()
                .map(debtPosition -> {
                    try {
                        Map<String, SyncCompleteDTO> iupdSyncStatusUpdateDTOMap = createIupdSyncStatusMap(debtPosition);
                        PaymentEventType paymentEventType = debtPositionOperationTypeResolver.calculateDebtPositionOperationType(debtPosition, iupdSyncStatusUpdateDTOMap);

                        WorkflowCreatedDTO workflowCreatedDTO = workflowDebtPositionService.syncDebtPosition(debtPosition, new WfExecutionParameters(), paymentEventType, "ingestionFlowFileId:" + ingestionFlowFileId);

                        if (workflowCreatedDTO == null) {
                            return null;
                        }
                        return Pair.of(debtPosition, workflowCreatedDTO);

                    } catch (Exception e) {
                        log.error("Error synchronizing debt position with id {} and iupdOrg {}: {}", debtPosition.getDebtPositionId(), debtPosition.getIupdOrg(), e.getMessage());
                        errors.append("\nError on debt position with iupdOrg ")
                                .append(debtPosition.getIupdOrg()).append(": ")
                                .append(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private Map<String, SyncCompleteDTO> createIupdSyncStatusMap(DebtPositionDTO debtPosition) {
        return debtPosition.getPaymentOptions().stream()
                .flatMap(paymentOption -> paymentOption.getInstallments().stream())
                .filter(installment -> InstallmentStatus.TO_SYNC.equals(installment.getStatus()) &&
                        installment.getSyncStatus() != null &&
                        installment.getSyncStatus().getSyncError() == null)
                .map(installment ->
                        Pair.of(installment.getIud(),
                                SyncCompleteDTO.builder()
                                        .newStatus(installment.getSyncStatus().getSyncStatusTo())
                                        .build())
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }
}
