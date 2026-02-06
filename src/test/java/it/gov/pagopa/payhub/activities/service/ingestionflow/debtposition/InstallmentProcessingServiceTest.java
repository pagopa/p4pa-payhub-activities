package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition.InstallmentSynchronizeMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionOrigin.ORDINARY_SIL;

@ExtendWith(MockitoExtension.class)
class InstallmentProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<InstallmentIngestionFlowFileDTO, InstallmentIngestionFlowFileResult, InstallmentErrorDTO> {

    @Mock
    private DebtPositionService debtPositionServiceMock;
    @Mock
    private InstallmentSynchronizeMapper installmentSynchronizeMapperMock;
    @Mock
    private InstallmentErrorsArchiverService installmentErrorsArchiverServiceMock;
    @Mock
    private DPInstallmentsWorkflowCompletionService dpInstallmentsWorkflowCompletionServiceMock;

    private InstallmentProcessingService serviceSpy;

    protected InstallmentProcessingServiceTest() {
        super(false);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new InstallmentProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                debtPositionServiceMock,
                installmentSynchronizeMapperMock,
                installmentErrorsArchiverServiceMock,
                dpInstallmentsWorkflowCompletionServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionServiceMock,
                installmentSynchronizeMapperMock,
                installmentErrorsArchiverServiceMock,
                dpInstallmentsWorkflowCompletionServiceMock,
                organizationServiceMock);
    }

    @Override
    protected IngestionFlowProcessingService<InstallmentIngestionFlowFileDTO, InstallmentIngestionFlowFileResult, InstallmentErrorDTO> getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<InstallmentErrorDTO> getErrorsArchiverServiceMock() {
        return installmentErrorsArchiverServiceMock;
    }

    @Override
    protected InstallmentIngestionFlowFileResult startProcess(Iterator<InstallmentIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processInstallments(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected InstallmentIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        InstallmentIngestionFlowFileDTO dto = podamFactory.manufacturePojo(InstallmentIngestionFlowFileDTO.class);
        dto.setIupdOrg("IUPDORG-" + sequencingId);
        dto.setIud("IUD-" + sequencingId);
        if (sequencingId > 1) {
            dto.setIupdOrg(null);
        }

        InstallmentSynchronizeDTO installmentSynchronizeDTO = podamFactory.manufacturePojo(InstallmentSynchronizeDTO.class);
        String workflowId = "workflow-123";
        WfExecutionParameters wfExecutionParameters = WfExecutionParameters.builder()
                .massive(true)
                .partialChange(true)
                .build();

        Mockito.doReturn(installmentSynchronizeDTO)
                .when(installmentSynchronizeMapperMock)
                .map(dto, ingestionFlowFile.getIngestionFlowFileId(), rowNumber, ingestionFlowFile.getOrganizationId(), ingestionFlowFile.getFileName());

        Mockito.doReturn(workflowId)
                .when(debtPositionServiceMock)
                .installmentSynchronize(ORDINARY_SIL, installmentSynchronizeDTO, wfExecutionParameters, ingestionFlowFile.getOperatorExternalId());

        Mockito.doReturn(Collections.emptyList())
                .when(dpInstallmentsWorkflowCompletionServiceMock)
                .waitForWorkflowCompletion(workflowId, dto, rowNumber, ingestionFlowFile.getFileName());

        return dto;
    }

    @Override
    protected List<Pair<InstallmentIngestionFlowFileDTO, List<InstallmentErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseWaitWorkflowErrors(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseDebtPositionNotFound(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<InstallmentIngestionFlowFileDTO, List<InstallmentErrorDTO>> configureUnhappyUseCaseWaitWorkflowErrors(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        InstallmentIngestionFlowFileDTO dto = podamFactory.manufacturePojo(InstallmentIngestionFlowFileDTO.class);
        InstallmentSynchronizeDTO installmentSynchronizeDTO = podamFactory.manufacturePojo(InstallmentSynchronizeDTO.class);
        String workflowId = "workflow-123";
        WfExecutionParameters wfExecutionParameters = WfExecutionParameters.builder()
                .massive(true)
                .partialChange(true)
                .build();

        Mockito.doReturn(installmentSynchronizeDTO)
                .when(installmentSynchronizeMapperMock)
                .map(dto, ingestionFlowFile.getIngestionFlowFileId(), rowNumber, ingestionFlowFile.getOrganizationId(), ingestionFlowFile.getFileName());

        Mockito.doReturn(workflowId)
                .when(debtPositionServiceMock)
                .installmentSynchronize(ORDINARY_SIL, installmentSynchronizeDTO, wfExecutionParameters, ingestionFlowFile.getOperatorExternalId());

        List<InstallmentErrorDTO> expectedErrors = List.of(InstallmentErrorDTO.builder().errorCode("DUMMY_ERROR").build());
        Mockito.doReturn(expectedErrors)
                .when(dpInstallmentsWorkflowCompletionServiceMock)
                .waitForWorkflowCompletion(workflowId, dto, rowNumber, ingestionFlowFile.getFileName());

        return Pair.of(dto, expectedErrors);
    }

    private Pair<InstallmentIngestionFlowFileDTO, List<InstallmentErrorDTO>> configureUnhappyUseCaseDebtPositionNotFound(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        InstallmentIngestionFlowFileDTO dto = podamFactory.manufacturePojo(InstallmentIngestionFlowFileDTO.class);
        InstallmentSynchronizeDTO installmentSynchronizeDTO = podamFactory.manufacturePojo(InstallmentSynchronizeDTO.class);
        WfExecutionParameters wfExecutionParameters = WfExecutionParameters.builder()
                .massive(true)
                .partialChange(true)
                .build();

        Mockito.doReturn(installmentSynchronizeDTO)
                .when(installmentSynchronizeMapperMock)
                .map(dto, ingestionFlowFile.getIngestionFlowFileId(), rowNumber, ingestionFlowFile.getOrganizationId(), ingestionFlowFile.getFileName());

        Mockito.doThrow(new RestClientException("[DEBT_POSITION_NOT_FOUND] debt position not found"))
                .when(debtPositionServiceMock)
                .installmentSynchronize(ORDINARY_SIL, installmentSynchronizeDTO, wfExecutionParameters, ingestionFlowFile.getOperatorExternalId());

        List<InstallmentErrorDTO> expectedErrors = List.of(
                new InstallmentErrorDTO(
                        ingestionFlowFile.getFileName(), dto.getIupdOrg(), dto.getIud(), null, rowNumber,
                        FileErrorCode.DEBT_POSITION_NOT_FOUND.name(),
                        FileErrorCode.DEBT_POSITION_NOT_FOUND.getMessage())
        );
        return Pair.of(dto, expectedErrors);
    }
}
