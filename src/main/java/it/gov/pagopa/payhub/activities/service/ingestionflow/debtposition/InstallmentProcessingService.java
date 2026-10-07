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
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStationDTO;
import it.gov.pagopa.pu.organization.dto.generated.PagoPaInteractionModel;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;

import static it.gov.pagopa.payhub.activities.enums.FileErrorCode.*;
import static it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition.InstallmentIngestionFlowFileRequiredFieldsValidator.setDefaultValues;
import static it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionOrigin.ORDINARY_SIL;

@Service
@Lazy
@Slf4j
public class InstallmentProcessingService extends IngestionFlowProcessingService<InstallmentIngestionFlowFileDTO, InstallmentIngestionFlowFileResult, InstallmentErrorDTO> {

    private final DebtPositionService debtPositionService;
    private final InstallmentSynchronizeMapper installmentSynchronizeMapper;
    private final DPInstallmentsWorkflowCompletionService dpInstallmentsWorkflowCompletionService;
    private final OrganizationService organizationService;

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
        this.organizationService = organizationService;
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
                                                                  Path workingDirectory,
                                                                  InstallmentIngestionFlowFileResult result) {
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        Optional<OrganizationStationDTO> organizationStationOpt = organizationService.getOrganizationStation(ingestionFlowFile.getOrganizationId(), null);

        if (organizationStationOpt.isEmpty()) {
            log.error("OrganizationStation for organization id {} not found", ingestionFlowFile.getOrganizationId());
            result.setErrorDescription(ORGANIZATION_STATION_NOT_FOUND.getMessage());
            return result;
        }

        result.setPagoPaInteractionModel(organizationStationOpt.get().getPagoPaInteractionModel());

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

        PagoPaInteractionModel pagoPaInteractionModel = ingestionFlowFileResult.getPagoPaInteractionModel();

        boolean isGpd = Objects.equals(pagoPaInteractionModel, PagoPaInteractionModel.ASYNC_GPD);
        Boolean flagPuPagoPaPayment = installment.getFlagPuPagoPaPayment();

        if (Boolean.FALSE.equals(flagPuPagoPaPayment) && isGpd && StringUtils.isBlank(installment.getIupdPagopa())) {
            return List.of(buildErrorDto(ingestionFlowFile, lineNumber, installment,
                    MISSING_IUPD_PAGOPA.name(), MISSING_IUPD_PAGOPA.getMessage()));
        }

        if (Boolean.TRUE.equals(flagPuPagoPaPayment) && StringUtils.isNotBlank(installment.getIupdPagopa())) {
            return List.of(buildErrorDto(ingestionFlowFile, lineNumber, installment,
                    INVALID_IUPD_PAGOPA.name(), INVALID_IUPD_PAGOPA.getMessage()));
        }

        String workflowId = debtPositionService.installmentSynchronize(ORDINARY_SIL, installmentSynchronizeDTO, wfExecutionParameters, ingestionFlowFile.getOperatorExternalId());
        return dpInstallmentsWorkflowCompletionService.waitForWorkflowCompletion(workflowId, installment, lineNumber);
    }

    @Override
    protected InstallmentErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, InstallmentIngestionFlowFileDTO row, String errorCode, String message) {
        return InstallmentErrorDTO.builder()
                .csvRow(row != null ? row.getRow() : null)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }

    @Override
    protected InstallmentErrorDTO buildReaderErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, String[] rawRow, String errorCode, String message) {
        return InstallmentErrorDTO.builder()
                .csvRow(lineNumber < 1 ? null : rawRow)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }
}

