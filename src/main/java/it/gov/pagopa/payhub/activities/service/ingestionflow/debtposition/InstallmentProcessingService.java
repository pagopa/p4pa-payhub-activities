package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition.InstallmentSynchronizeMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Service
@Lazy
@Slf4j
public class InstallmentProcessingService {

    private final DebtPositionService debtPositionService;
    private final InstallmentSynchronizeMapper installmentSynchronizeMapper;
    private final InstallmentErrorsArchiverService installmentErrorsArchiverService;
    private final WorkflowCompletionService workflowCompletionService;

    public InstallmentProcessingService(DebtPositionService debtPositionService,
                                        InstallmentSynchronizeMapper installmentSynchronizeMapper,
                                        InstallmentErrorsArchiverService installmentErrorsArchiverService,
                                        WorkflowCompletionService workflowCompletionService) {
        this.debtPositionService = debtPositionService;
        this.installmentSynchronizeMapper = installmentSynchronizeMapper;
        this.installmentErrorsArchiverService = installmentErrorsArchiverService;
        this.workflowCompletionService = workflowCompletionService;
    }

    /**
     * Processes a stream of InstallmentIngestionFlowFileDTO and synchronizes each installment.
     *
     * @param installmentIngestionFlowFileDTOStream Stream of installment ingestion flow file DTOs to be processed.
     * @param ingestionFlowFile Metadata of the ingestion file containing details about the ingestion process.
     * @param workingDirectory The directory where error files will be written if processing fails.
     * @return An {@link InstallmentIngestionFlowFileResult} containing details about the processed rows, errors, and archived files.
     */
    public InstallmentIngestionFlowFileResult processInstallments(Stream<InstallmentIngestionFlowFileDTO> installmentIngestionFlowFileDTOStream,
                                                                  IngestionFlowFile ingestionFlowFile,
                                                                  Path workingDirectory) {
        List<InstallmentErrorDTO> errorList = new ArrayList<>();
        AtomicLong processedRows = new AtomicLong(0);

        long totalRows = installmentIngestionFlowFileDTOStream
                .filter(installmentIngestionFlowFileDTO -> {
                    InstallmentSynchronizeDTO installmentSynchronizeDTO = installmentSynchronizeMapper.map(
                            installmentIngestionFlowFileDTO,
                            ingestionFlowFile.getIngestionFlowFileId(),
                            ingestionFlowFile.getOrganizationId()
                    );
                    try {
                        // see massive
                        String workflowId = debtPositionService.installmentSynchronize(installmentSynchronizeDTO, false);
                        if (workflowId != null) {
                            boolean workflowCompleted = workflowCompletionService.waitForWorkflowCompletion(workflowId, installmentIngestionFlowFileDTO, ingestionFlowFile.getFileName(), errorList);
                            log.info("Workflow with id {} completed", workflowId);
                            if (workflowCompleted) {
                                processedRows.incrementAndGet();
                            }
                            return workflowCompleted;
                        }
                        processedRows.incrementAndGet();
                        return true;
                    } catch (Exception e) {
                        log.error("Error processing installment {}: {}", installmentIngestionFlowFileDTO.getIud(), e.getMessage());
                        InstallmentErrorDTO error = workflowCompletionService.buildInstallmentErrorDTO(ingestionFlowFile.getFileName(), installmentIngestionFlowFileDTO, null, "PROCESS_EXCEPTION", e.getMessage());
                        errorList.add(error);
                        log.info("Current error list size after handleProcessingError: {}", errorList.size());
                        return false;
                    }
                })
                .count();

        String errorsZipFileName = null;
        String discardedFilePath = null;
        if (!errorList.isEmpty()) {
            installmentErrorsArchiverService.writeErrors(workingDirectory, ingestionFlowFile, errorList);
            errorsZipFileName = installmentErrorsArchiverService.archiveErrorFiles(workingDirectory, ingestionFlowFile);
            if (errorsZipFileName != null) {
                Path targetDirectory = installmentErrorsArchiverService.createTargetDirectory(ingestionFlowFile);
                discardedFilePath = targetDirectory.resolve(errorsZipFileName).toString();
            }

            log.info("Error file archived at: {}", errorsZipFileName);
        }

        return new InstallmentIngestionFlowFileResult(
                totalRows,
                processedRows.get(),
                errorsZipFileName != null ? "Some rows have failed" : null,
                errorsZipFileName,
                discardedFilePath
        );
    }
}

