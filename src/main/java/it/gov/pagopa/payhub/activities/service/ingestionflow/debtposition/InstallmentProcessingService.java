package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
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

import static it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionOrigin.ORDINARY_SIL;

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

            try {
                InstallmentSynchronizeDTO installmentSynchronizeDTO = installmentSynchronizeMapper.map(
                        installment,
                        ingestionFlowFile.getIngestionFlowFileId(),
                        totalRows,
                        ingestionFlowFile.getOrganizationId()
                );
                WfExecutionParameters wfExecutionParameters = new WfExecutionParameters();
                wfExecutionParameters.setMassive(true);
                wfExecutionParameters.setPartialChange(true);

                String workflowId = debtPositionService.installmentSynchronize(ORDINARY_SIL, installmentSynchronizeDTO, wfExecutionParameters, ingestionFlowFile.getOperatorExternalId());
                if (dpInstallmentsWorkflowCompletionService.waitForWorkflowCompletion(workflowId, installment, totalRows, ingestionFlowFile.getFileName(), errorList)) {
                    processedRows++;
                }

            } catch (Exception e) {
                log.error("Error processing installment {}: {}", installment.getIud(), e.getMessage());
                InstallmentErrorDTO error = new InstallmentErrorDTO(
                        ingestionFlowFile.getFileName(),
                        installment.getIupdOrg(), installment.getIud(), null,
                        totalRows, "PROCESS_EXCEPTION", e.getMessage());
                errorList.add(error);
                log.info("Current error list size after handleProcessingError: {}", errorList.size());
            }
        }

        String errorsZipFileName = archiveErrorFiles(ingestionFlowFile, workingDirectory, errorList);
        return InstallmentIngestionFlowFileResult.builder()
                .totalRows(totalRows)
                .processedRows(processedRows)
                .errorDescription(errorsZipFileName != null ? "Some rows have failed" : null)
                .discardedFileName(errorsZipFileName)
                .build();
    }

    private String archiveErrorFiles(IngestionFlowFile ingestionFlowFile, Path workingDirectory, List<InstallmentErrorDTO> errorList) {
        if (errorList.isEmpty()) {
            log.info("No errors to archive for file: {}", ingestionFlowFile.getFileName());
            return null;
        }

        installmentErrorsArchiverService.writeErrors(workingDirectory, ingestionFlowFile, errorList);
        String errorsZipFileName = installmentErrorsArchiverService.archiveErrorFiles(workingDirectory, ingestionFlowFile);
        log.info("Error file archived at: {}", errorsZipFileName);

        return errorsZipFileName;
    }
}

