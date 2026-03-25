package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowDebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.SyncIngestedDebtPositionDTO;
import it.gov.pagopa.payhub.activities.service.debtposition.DebtPositionOperationTypeResolver;
import it.gov.pagopa.payhub.activities.service.exportflow.debtposition.IUVArchivingExportFileService;
import it.gov.pagopa.payhub.activities.service.pagopapayments.GenerateNoticeService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static io.temporal.api.enums.v1.WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED;

@Slf4j
@Lazy
@Service
public class SynchronizeIngestedDebtPositionActivityImpl implements SynchronizeIngestedDebtPositionActivity {

    private final DebtPositionService debtPositionService;
    private final WorkflowDebtPositionService workflowDebtPositionService;
    private final WorkflowHubService workflowHubService;
    private final GenerateNoticeService generateNoticeService;
    private final DebtPositionOperationTypeResolver debtPositionOperationTypeResolver;
    private final IngestionFlowFileService ingestionFlowFileService;

    private final Integer pageSize;
    private final int maxAttempts;
    private final int retryDelayMs;
    private final IUVArchivingExportFileService iuvArchivingExportFileService;

    private static final List<String> DEFAULT_ORDERING = List.of("debtPositionId,asc");
    private static final int MAX_NOTICES_PER_CALL = 1000;

    public SynchronizeIngestedDebtPositionActivityImpl(DebtPositionService debtPositionService, WorkflowDebtPositionService workflowDebtPositionService, WorkflowHubService workflowHubService, GenerateNoticeService generateNoticeService, DebtPositionOperationTypeResolver debtPositionOperationTypeResolver, IngestionFlowFileService ingestionFlowFileService,
                                                       @Value("${query-limits.debt-positions.size}") Integer pageSize,
                                                       @Value("${ingestion-flow-files.dp-installments.wf-await.max-waiting-minutes}") int maxWaitingMinutes,
                                                       @Value("${ingestion-flow-files.dp-installments.wf-await.retry-delays-ms}") int retryDelayMs, IUVArchivingExportFileService iuvArchivingExportFileService) {
        this.debtPositionService = debtPositionService;
        this.workflowDebtPositionService = workflowDebtPositionService;
        this.workflowHubService = workflowHubService;
        this.generateNoticeService = generateNoticeService;
        this.debtPositionOperationTypeResolver = debtPositionOperationTypeResolver;
        this.ingestionFlowFileService = ingestionFlowFileService;
        this.pageSize = pageSize;
        this.iuvArchivingExportFileService = iuvArchivingExportFileService;
        this.maxAttempts = (int) (((double) maxWaitingMinutes * 60_000) / retryDelayMs);
        this.retryDelayMs = retryDelayMs;
    }

    @Override
    public SyncIngestedDebtPositionDTO synchronizeIngestedDebtPosition(Long ingestionFlowFileId) {
        log.info("Synchronizing all debt positions related to ingestion flow file id {}", ingestionFlowFileId);

        StringBuilder errors = new StringBuilder();

        int currentPage = 0;
        boolean hasMorePages = true;

        Map<String, DebtPositionDTO> iuvToDebtPositionMap = new LinkedHashMap<>();
        List<DebtPositionDTO> debtPositionsExportIuv = new ArrayList<>();
        List<InstallmentStatus> statusToExclude = List.of(InstallmentStatus.DRAFT);

        while (hasMorePages) {
            PagedDebtPositions pagedDebtPositions = debtPositionService.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId,
                    statusToExclude,
                    currentPage,
                    pageSize,
                    DEFAULT_ORDERING);

            log.info("Synchronizing page {} of {} retrieved searching debt positions related to ingestionFlowFileId {} (totalElements {})",
                    currentPage+1, pagedDebtPositions.getTotalPages(), ingestionFlowFileId, pagedDebtPositions.getTotalElements());
            List<Pair<DebtPositionDTO, WorkflowCreatedDTO>> wfIds = syncDebtPositions(ingestionFlowFileId, pagedDebtPositions, errors);

            wfIds.forEach(p -> {
                WorkflowCreatedDTO workflow = p.getRight();
                DebtPositionDTO debtPosition = p.getLeft();
                try {
                    WorkflowStatusDTO workflowExecutionStatus = workflowHubService.waitWorkflowCompletion(workflow.getWorkflowId(), maxAttempts, retryDelayMs);

                    if (!WORKFLOW_EXECUTION_STATUS_COMPLETED.equals(workflowExecutionStatus.getStatus()) ||
                            (workflowExecutionStatus.getResult() != null && !workflowExecutionStatus.getResult().contains("\"iupdSyncError\":{}"))) {
                        errors.append("\nSynchronization workflow for debt position with iupdOrg ")
                                .append(debtPosition.getIupdOrg())
                                .append(" terminated with error status.");
                    } else {
                        if (debtPosition.getFlagPuPagoPaPayment()) {
                            debtPositionsExportIuv.add(debtPosition);
                            addIuvToGenerateNoticeMap(ingestionFlowFileId, debtPosition, iuvToDebtPositionMap);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error waiting for debt position sync workflowId with id {} and iupdOrg {}", workflow.getWorkflowId(), debtPosition.getIupdOrg(), e);
                    errors.append("\nError on debt position with iupdOrg ")
                            .append(debtPosition.getIupdOrg()).append(": ")
                            .append(e.getMessage());
                }
            });

            currentPage++;
            hasMorePages = currentPage < pagedDebtPositions.getTotalPages();
        }

        log.info("Synchronization of all debt positions related to ingestion flow file id {} completed", ingestionFlowFileId);

        String pdfGeneratedId = retrievePdfGeneratedIdFromGenerateNotice(ingestionFlowFileId, iuvToDebtPositionMap, errors);

        if (pdfGeneratedId != null) {
            ingestionFlowFileService.updatePdfGenerated(
                    ingestionFlowFileId,
                    (long) iuvToDebtPositionMap.size(),
                    pdfGeneratedId
            );
        }

        Path csvPath = createIuvArchivingExportFile(ingestionFlowFileId, debtPositionsExportIuv);

        return SyncIngestedDebtPositionDTO.builder()
                .errorsDescription(errors.toString())
                .pdfGeneratedId(pdfGeneratedId)
                .iuvFileName(csvPath != null ? csvPath.getFileName().toString() : null)
                .build();
    }

    private Path createIuvArchivingExportFile(Long ingestionFlowFileId, List<DebtPositionDTO> debtPositionExportIuv) {
        if (debtPositionExportIuv.isEmpty()) {
            return null;
        }
        return iuvArchivingExportFileService.executeExport(debtPositionExportIuv, ingestionFlowFileId);
    }

    private String retrievePdfGeneratedIdFromGenerateNotice(Long ingestionFlowFileId, Map<String, DebtPositionDTO> iuvToDebtPositionMap, StringBuilder errors) {
        if (iuvToDebtPositionMap.isEmpty()) {
            return null;
        }

        List<String> folderIds = new ArrayList<>();

        try {
            List<String> allIuvs = new ArrayList<>(iuvToDebtPositionMap.keySet());
            List<List<String>> iuvchunks = ListUtils.partition(allIuvs, MAX_NOTICES_PER_CALL);

            for (List<String> iuvchunk : iuvchunks) {
                List<DebtPositionDTO> filteredDebtPositions = iuvchunk.stream()
                        .map(iuvToDebtPositionMap::get)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

                if (filteredDebtPositions.isEmpty()) {
                    continue;
                }

                String folderId = generateNoticeService.generateNotices(ingestionFlowFileId, filteredDebtPositions, iuvchunk);

                if (folderId != null) {
                    folderIds.add(folderId);
                }
            }
        } catch (Exception e) {
            log.error("Error calling generateMassiveNotices for ingestionFlowFileId: {}", ingestionFlowFileId, e);
            errors.append("\nError on generate notice massive for ingestionFlowFileId ")
                    .append(ingestionFlowFileId).append(": ")
                    .append(e.getMessage());
        }

        return folderIds.isEmpty() ? null : String.join(",", folderIds);
    }

    private void addIuvToGenerateNoticeMap(Long ingestionFlowFileId, DebtPositionDTO debtPosition, Map<String, DebtPositionDTO> iuvToDebtPositionMap) {
        debtPosition.getPaymentOptions().stream()
                .flatMap(po -> po.getInstallments().stream())
                .filter(installment ->
                        Boolean.TRUE.equals(installment.getGenerateNotice()) &&
                                ingestionFlowFileId.equals(installment.getIngestionFlowFileId()) &&
                                !InstallmentStatus.CANCELLED.equals(
                                        Objects.requireNonNull(installment.getSyncStatus()).getSyncStatusTo()))
                .map(InstallmentDTO::getIuv)
                .forEach(iuv -> iuvToDebtPositionMap.put(iuv, debtPosition));
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
                        log.error("Error synchronizing debt position with id {} and iupdOrg {}", debtPosition.getDebtPositionId(), debtPosition.getIupdOrg(), e);
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
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }
}
