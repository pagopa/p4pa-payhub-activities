package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition.mapper.InstallmentSynchronizeMapper;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.WorkflowStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@Lazy
@Slf4j
public class InstallmentProcessingService {

    private final DebtPositionService debtPositionService;
    private final WorkflowHubService workflowHubService;
    private final InstallmentSynchronizeMapper installmentSynchronizeMapper;
    private final InstallmentErrorsArchiverService installmentErrorsArchiverService;

    private final int maxRetries;
    private final int retryDelayMs;

    public InstallmentProcessingService(DebtPositionService debtPositionService,
                                        WorkflowHubService workflowHubService,
                                        InstallmentSynchronizeMapper installmentSynchronizeMapper, InstallmentErrorsArchiverService installmentErrorsArchiverService,
                                        @Value("${workflow.max-retries}") int maxRetries,
                                        @Value("${workflow.retry-delay-ms}") int retryDelayMs) {
        this.debtPositionService = debtPositionService;
        this.workflowHubService = workflowHubService;
        this.installmentSynchronizeMapper = installmentSynchronizeMapper;
        this.installmentErrorsArchiverService = installmentErrorsArchiverService;
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
    }

    /**
     * Processes a stream of InstallmentIngestionFlowFileDTO and synchronizes each installment.
     *
     * @param installmentIngestionFlowFileDTOStream Stream of InstallmentIngestionFlowFileDTO
     * @param ingestionFlowFileDTO Metadata of the ingestion file
     * @return List of processed InstallmentSynchronizeDTO
     */
    public InstallmentIngestionFlowFileResult processInstallments(Stream<InstallmentIngestionFlowFileDTO> installmentIngestionFlowFileDTOStream,
                                                                  IngestionFlowFile ingestionFlowFileDTO,
                                                                  Path workingDirectory,
                                                                  long totalRows) {
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        long processedRows = installmentIngestionFlowFileDTOStream
                .map(installmentIngestionFlowFileDTO -> {
                    InstallmentSynchronizeDTO installmentSynchronizeDTO = installmentSynchronizeMapper.map(
                            installmentIngestionFlowFileDTO,
                            ingestionFlowFileDTO.getIngestionFlowFileId(),
                            ingestionFlowFileDTO.getOrganizationId()
                    );
                    try {
                        String workflowId = debtPositionService.installmentSynchronize(installmentSynchronizeDTO, true);
                        if (workflowId != null) {
                            waitForWorkflowCompletion(workflowId, installmentIngestionFlowFileDTO, ingestionFlowFileDTO, errorList);
                            log.info("Workflow with id {} completed", workflowId);
                        }

                        return true;
                    } catch (Exception e) {
                        log.error("Error processing installment {}: {}", installmentIngestionFlowFileDTO.getIud(), e.getMessage());
                        errorList.add(buildInstallmentErrorDTO(ingestionFlowFileDTO.getFileName(), installmentIngestionFlowFileDTO, "EXCEPTION", e.getMessage()));
                        return false;
                    }
                })
                .filter(success -> success)
                .count();

        String zipFileName = null;
        if (!errorList.isEmpty()) {
            installmentErrorsArchiverService.writeInstallmentErrors(workingDirectory, ingestionFlowFileDTO, errorList);
            zipFileName = installmentErrorsArchiverService.archiveInstallmentErrors(workingDirectory, ingestionFlowFileDTO);
            log.info("Error file archived at: {}", zipFileName);
        }

        return new InstallmentIngestionFlowFileResult(
                totalRows,
                processedRows,
                zipFileName != null ? "Some rows have failed" : null,
                zipFileName,
                workingDirectory.getParent().toString()
        );
    }

    /**
     * Waits for a workflow to reach a terminal status.
     *
     * @param workflowId The ID of the workflow
     */
    private void waitForWorkflowCompletion(String workflowId, InstallmentIngestionFlowFileDTO installment,
                                              IngestionFlowFile ingestionFlowFileDTO, List<InstallmentErrorDTO> errorList) {
        int attempts = 0;
        WorkflowStatus status;

        do {
            WorkflowStatusDTO statusDTO = workflowHubService.getWorkflowStatus(workflowId);
            status = WorkflowStatus.fromString(statusDTO.getStatus());

            log.info("Workflow {} status: {}", workflowId, status);

            if (status != null && status.isTerminal()) {
                if (status != WorkflowStatus.COMPLETED) {
                    errorList.add(buildInstallmentErrorDTO(ingestionFlowFileDTO.getFileName(), installment, status.name(), "Workflow terminated with error status"));
                }
                return;
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
        errorList.add(buildInstallmentErrorDTO(ingestionFlowFileDTO.getFileName(), installment, "RETRY_LIMIT_REACHED", "Maximum number of retries reached"));
    }


    private InstallmentErrorDTO buildInstallmentErrorDTO(String fileName, InstallmentIngestionFlowFileDTO installment, String workflowStatus, String errorMessage) {
        return new InstallmentErrorDTO(
                fileName,
                installment.getIupdOrg(),
                installment.getIud(),
                workflowStatus,
                installment.getIngestionFlowFileLineNumber(),
                "PROCESSING_ERROR",
                errorMessage
        );
    }
}

