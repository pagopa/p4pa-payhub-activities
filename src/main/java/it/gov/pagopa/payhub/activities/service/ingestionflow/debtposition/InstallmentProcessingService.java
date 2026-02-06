package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition.InstallmentSynchronizeMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition.InstallmentIngestionFlowFileRequiredFieldsValidator.setDefaultValues;
import static it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionOrigin.ORDINARY_SIL;

@Service
@Lazy
@Slf4j
public class InstallmentProcessingService extends IngestionFlowProcessingService<InstallmentIngestionFlowFileDTO, InstallmentIngestionFlowFileResult, InstallmentErrorDTO> {

    private final DebtPositionService debtPositionService;
    private final InstallmentSynchronizeMapper installmentSynchronizeMapper;
    private final DPInstallmentsWorkflowCompletionService dpInstallmentsWorkflowCompletionService;

    public InstallmentProcessingService(
            @Value("${ingestion-flow-files.dp-installments.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            DebtPositionService debtPositionService,
            InstallmentSynchronizeMapper installmentSynchronizeMapper,
            InstallmentErrorsArchiverService installmentErrorsArchiverService,
            DPInstallmentsWorkflowCompletionService dpInstallmentsWorkflowCompletionService,
            OrganizationService organizationService, FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, installmentErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.debtPositionService = debtPositionService;
        this.installmentSynchronizeMapper = installmentSynchronizeMapper;
        this.dpInstallmentsWorkflowCompletionService = dpInstallmentsWorkflowCompletionService;
    }

    /**
     * Processes a stream of InstallmentIngestionFlowFileDTO and synchronizes each installment.
     *
     * @param iterator          Stream of installment ingestion flow file DTOs to be processed.
     * @param readerExceptions  A list which will collect the exceptions thrown during iterator processing
     * @param ingestionFlowFile Metadata of the ingestion file containing details about the ingestion process.
     * @param workingDirectory  The directory where error files will be written if processing fails.
     * @return An {@link InstallmentIngestionFlowFileResult} containing details about the processed rows, errors, and archived files.
     */
    public InstallmentIngestionFlowFileResult processInstallments(Iterator<InstallmentIngestionFlowFileDTO> iterator,
                                                                  List<CsvException> readerExceptions,
                                                                  IngestionFlowFile ingestionFlowFile,
                                                                  Path workingDirectory) {
        List<InstallmentErrorDTO> errorList = new ArrayList<>();
        InstallmentIngestionFlowFileResult result = new InstallmentIngestionFlowFileResult();
        process(iterator, readerExceptions, result, ingestionFlowFile, errorList, workingDirectory);
        return result;
    }

    @Override
    protected String getSequencingId(InstallmentIngestionFlowFileDTO row) {
        return Objects.requireNonNullElse(row.getIupdOrg(), row.getIud());
    }

    @Override
    protected List<InstallmentErrorDTO> consumeRow(long lineNumber,
                                                   InstallmentIngestionFlowFileDTO installment,
                                                   InstallmentIngestionFlowFileResult ingestionFlowFileResult,
                                                   IngestionFlowFile ingestionFlowFile) {
        setDefaultValues(installment);
        InstallmentSynchronizeDTO installmentSynchronizeDTO = installmentSynchronizeMapper.map(
                installment,
                ingestionFlowFile.getIngestionFlowFileId(),
                lineNumber,
                ingestionFlowFile.getOrganizationId(),
                ingestionFlowFile.getFileName()
        );
        WfExecutionParameters wfExecutionParameters = new WfExecutionParameters();
        wfExecutionParameters.setMassive(true);
        wfExecutionParameters.setPartialChange(true);

        String workflowId = debtPositionService.installmentSynchronize(ORDINARY_SIL, installmentSynchronizeDTO, wfExecutionParameters, ingestionFlowFile.getOperatorExternalId());
        return dpInstallmentsWorkflowCompletionService.waitForWorkflowCompletion(workflowId, installment, lineNumber, ingestionFlowFile.getFileName());
    }

    @Override
    protected InstallmentErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, InstallmentIngestionFlowFileDTO row, String errorCode, String message) {
        InstallmentErrorDTO errorDTO = InstallmentErrorDTO.builder()
                .fileName(ingestionFlowFile.getFileName())
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
        if (row != null) {
            errorDTO.setIupdOrg(row.getIupdOrg());
            errorDTO.setIud(row.getIud());
        }
        return errorDTO;
    }

}

