package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition.mapper.InstallmentSynchronizeMapper;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileResult;
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

import static it.gov.pagopa.payhub.activities.dto.debtposition.constants.WorkflowStatus.TERMINAL_STATUSES;

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
     * @param ingestionFlowFile Metadata of the ingestion file
     * @return List of processed InstallmentSynchronizeDTO
     */
    public InstallmentIngestionFlowFileResult processInstallments(Stream<InstallmentIngestionFlowFileDTO> installmentIngestionFlowFileDTOStream,
                                                                  IngestionFlowFile ingestionFlowFile,
                                                                  Path workingDirectory,
                                                                  long totalRows) {
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        long processedRows = installmentIngestionFlowFileDTOStream
                .map(installmentIngestionFlowFileDTO -> {
                    InstallmentSynchronizeDTO installmentSynchronizeDTO = installmentSynchronizeMapper.map(
                            installmentIngestionFlowFileDTO,
                            ingestionFlowFile.getIngestionFlowFileId(),
                            ingestionFlowFile.getOrganizationId()
                    );
                    try {
                        // see massive
                        String workflowId = debtPositionService.installmentSynchronize(installmentSynchronizeDTO, false);
                        if (workflowId != null) {
                            boolean workflowCompleted = waitForWorkflowCompletion(workflowId, installmentIngestionFlowFileDTO, ingestionFlowFile.getFileName(), errorList);
                            log.info("Workflow with id {} completed", workflowId);
                            return workflowCompleted;
                        }

                        return true;
                    } catch (Exception e) {
                        log.error("Error processing installment {}: {}", installmentIngestionFlowFileDTO.getIud(), e.getMessage());
                        errorList.add(buildInstallmentErrorDTO(ingestionFlowFile.getFileName(), installmentIngestionFlowFileDTO, null, "PROCESS_EXCEPTION", e.getMessage()));
                        return false;
                    }
                })
                .filter(success -> success)
                .count();

        String zipFileName = null;
        if (!errorList.isEmpty()) {
            installmentErrorsArchiverService.writeErrors(workingDirectory, ingestionFlowFile, errorList);
            zipFileName = installmentErrorsArchiverService.archiveErrorFiles(workingDirectory, ingestionFlowFile);
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
    private boolean waitForWorkflowCompletion(String workflowId, InstallmentIngestionFlowFileDTO installment,
                                              String fileName, List<InstallmentErrorDTO> errorList) {
        int attempts = 0;
        String status;

        do {
            WorkflowStatusDTO statusDTO = workflowHubService.getWorkflowStatus(workflowId);
            status = statusDTO.getStatus();
            log.info("Workflow {} status: {}", workflowId, status);

            if (status != null && TERMINAL_STATUSES.contains(status)) {
                if (!"COMPLETED".equals(status)) {
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

    private InstallmentErrorDTO buildInstallmentErrorDTO(String fileName, InstallmentIngestionFlowFileDTO installment, String workflowStatus, String code, String errorMessage) {
        return InstallmentErrorDTO.builder()
                .fileName(fileName)
                .iupdOrg(installment.getIupdOrg())
                .iud(installment.getIud())
                .workflowStatus(workflowStatus)
                .rowNumber(installment.getIngestionFlowFileLineNumber())
                .errorCode(code)
                .errorMessage(errorMessage)
                .build();
    }

}

