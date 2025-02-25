package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition.InstallmentSynchronizeMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Lazy
@Slf4j
public class InstallmentProcessingService {

    private final DebtPositionService debtPositionService;
    private final InstallmentSynchronizeMapper installmentSynchronizeMapper;
    private final InstallmentErrorsArchiverService installmentErrorsArchiverService;
    private final DPInstallmentsWorkflowCompletionService dpInstallmentsWorkflowCompletionService;

    public InstallmentProcessingService(DebtPositionService debtPositionService,
                                        InstallmentSynchronizeMapper installmentSynchronizeMapper,
                                        InstallmentErrorsArchiverService installmentErrorsArchiverService,
                                        DPInstallmentsWorkflowCompletionService dpInstallmentsWorkflowCompletionService) {
        this.debtPositionService = debtPositionService;
        this.installmentSynchronizeMapper = installmentSynchronizeMapper;
        this.installmentErrorsArchiverService = installmentErrorsArchiverService;
        this.dpInstallmentsWorkflowCompletionService = dpInstallmentsWorkflowCompletionService;
    }

    /**
     * Processes a stream of InstallmentIngestionFlowFileDTO and synchronizes each installment.
     *
     * @param iterator          Stream of installment ingestion flow file DTOs to be processed.
     * @param ingestionFlowFile Metadata of the ingestion file containing details about the ingestion process.
     * @param workingDirectory  The directory where error files will be written if processing fails.
     * @return An {@link InstallmentIngestionFlowFileResult} containing details about the processed rows, errors, and archived files.
     */
    public InstallmentIngestionFlowFileResult processInstallments(Iterator<InstallmentIngestionFlowFileDTO> iterator,
                                                                  IngestionFlowFile ingestionFlowFile,
                                                                  Path workingDirectory) {
        List<InstallmentErrorDTO> errorList = new ArrayList<>();
        long processedRows = 0;
        long totalRows = 0;

        while (iterator.hasNext()) {
            totalRows++;

            InstallmentIngestionFlowFileDTO installment = iterator.next();
            InstallmentSynchronizeDTO installmentSynchronizeDTO = installmentSynchronizeMapper.map(
                    installment,
                    ingestionFlowFile.getIngestionFlowFileId(),
                    ingestionFlowFile.getOrganizationId()
            );
            try {
                // For the moment we have decided to call the GPD api for a single DP because their development for the massive v2.0 is not yet ready
                String workflowId = debtPositionService.installmentSynchronize(installmentSynchronizeDTO, false);
                if (workflowId != null) {
                    boolean workflowCompleted = dpInstallmentsWorkflowCompletionService.waitForWorkflowCompletion(
                            workflowId, installment, ingestionFlowFile.getFileName(), errorList
                    );
                    log.info("Workflow with id {} completed", workflowId);
                    if (workflowCompleted) {
                        processedRows++;
                    }
                } else {
                    processedRows++;
                }
            } catch (Exception e) {
                log.error("Error processing installment {}: {}", installment.getIud(), e.getMessage());
                InstallmentErrorDTO error = dpInstallmentsWorkflowCompletionService.buildInstallmentErrorDTO(
                        ingestionFlowFile.getFileName(), installment, null, "PROCESS_EXCEPTION", e.getMessage());
                errorList.add(error);
                log.info("Current error list size after handleProcessingError: {}", errorList.size());
            }
        }

        String errorsZipFileName = null;
        if (!errorList.isEmpty()) {
            installmentErrorsArchiverService.writeErrors(workingDirectory, ingestionFlowFile, errorList);
            errorsZipFileName = installmentErrorsArchiverService.archiveErrorFiles(workingDirectory, ingestionFlowFile);
            log.info("Error file archived at: {}", errorsZipFileName);
        }

        return new InstallmentIngestionFlowFileResult(
                totalRows,
                processedRows,
                errorsZipFileName != null ? "Some rows have failed" : null,
                errorsZipFileName
        );
    }
}

