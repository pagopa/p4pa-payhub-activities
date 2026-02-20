package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;


import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt.ReceiptMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ReceiptProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<ReceiptIngestionFlowFileDTO, ReceiptIngestionFlowFileResult, ReceiptErrorDTO> {

    @Mock
    private ReceiptErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private ReceiptService receiptServiceMock;
    @Mock
    private ReceiptMapper mapperMock;
    @Mock
    private ReceiptIngestionFlowFileRequiredFieldsValidatorService requiredFieldsValidatorServiceMock;

    private ReceiptProcessingService serviceSpy;

    protected ReceiptProcessingServiceTest() {
        super(false);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new ReceiptProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                mapperMock,
                errorsArchiverServiceMock,
                receiptServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService,
                requiredFieldsValidatorServiceMock
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                receiptServiceMock,
                organizationServiceMock,
                requiredFieldsValidatorServiceMock
        );
    }

    @Override
    protected ReceiptProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<ReceiptErrorDTO, ReceiptIngestionFlowFileResult> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected ReceiptIngestionFlowFileResult startProcess(Iterator<ReceiptIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processReceipts(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory, new ReceiptIngestionFlowFileResult());
    }

    @Override
    protected ReceiptIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        ReceiptIngestionFlowFileDTO dto = podamFactory.manufacturePojo(ReceiptIngestionFlowFileDTO.class);
        dto.setIuv("IUV" + sequencingId);
        dto.setCreditorReferenceId(dto.getIuv());

        ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = podamFactory.manufacturePojo(ReceiptWithAdditionalNodeDataDTO.class);

        Mockito.doNothing()
                .when(requiredFieldsValidatorServiceMock)
                .validateIngestionFile(ingestionFlowFile, dto);
        Mockito.doReturn(receiptWithAdditionalNodeDataDTO)
                .when(mapperMock)
                .map(ingestionFlowFile, dto);
        Mockito.doReturn(new ReceiptDTO())
                .when(receiptServiceMock)
                .createReceipt(receiptWithAdditionalNodeDataDTO);

        return dto;
    }

    @Override
    protected List<Pair<ReceiptIngestionFlowFileDTO, List<ReceiptErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseValidatorFail(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<ReceiptIngestionFlowFileDTO, List<ReceiptErrorDTO>> configureUnhappyUseCaseValidatorFail(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        ReceiptIngestionFlowFileDTO dto = podamFactory.manufacturePojo(ReceiptIngestionFlowFileDTO.class);
        dto.setOrgFiscalCode(organization.getOrgFiscalCode());
        dto.setFiscalCodePA(organization.getOrgFiscalCode());

        Mockito.doAnswer(a -> {
                    ReceiptIngestionFlowFileDTO validatingRow = a.getArgument(1);
                    Mockito.doReturn(Optional.of(organization)).when(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
                    new ReceiptIngestionFlowFileRequiredFieldsValidatorService(organizationServiceMock, null).validateIngestionFile(ingestionFlowFile, validatingRow);
                    return true;
                })
                .when(requiredFieldsValidatorServiceMock)
                .validateIngestionFile(ingestionFlowFile, dto);

        List<ReceiptErrorDTO> expectedErrors = List.of(
                ReceiptErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("RECEIPT_IUV_MISMATCH")
                        .errorMessage("I campi codIuv e identificativoUnivocoVersamento devono essere uguali")
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }
}