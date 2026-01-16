package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;


import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt.ReceiptMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReceiptProcessingServiceTest {

    @Mock
    private ReceiptErrorsArchiverService errorsArchiverServiceMock;

    @Mock
    private ReceiptService receiptServiceMock;

    @Mock
    private Path workingDirectory;

    @Mock
    private ReceiptMapper mapperMock;

    @Mock
    private OrganizationService organizationServiceMock;

    @Mock
    private ReceiptIngestionFlowFileRequiredFieldsValidatorService requiredFieldsValidatorServiceMock;

    private ReceiptProcessingService service;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @BeforeEach
    void setUp() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        service = new ReceiptProcessingService(mapperMock, errorsArchiverServiceMock, receiptServiceMock, organizationServiceMock, fileExceptionHandlerService, requiredFieldsValidatorServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                receiptServiceMock);
    }

    @Test
    void whenProcessReceiptThenOk() {
        //given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ReceiptIngestionFlowFileDTO dto = podamFactory.manufacturePojo(ReceiptIngestionFlowFileDTO.class);
        dto.setIuv("IUV");
        dto.setCreditorReferenceId("IUV");
        ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = podamFactory.manufacturePojo(ReceiptWithAdditionalNodeDataDTO.class);

        Mockito.doNothing().when(requiredFieldsValidatorServiceMock).validateIngestionFile(ingestionFlowFile, dto);
        Mockito.when(mapperMock.map(ingestionFlowFile, dto)).thenReturn(receiptWithAdditionalNodeDataDTO);
        Mockito.when(receiptServiceMock.createReceipt(receiptWithAdditionalNodeDataDTO)).thenReturn(new ReceiptDTO());

        //when
        ReceiptIngestionFlowFileResult result = service.processReceipts(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        //then
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Mockito.verify(mapperMock).map(ingestionFlowFile, dto);
        Mockito.verify(receiptServiceMock).createReceipt(receiptWithAdditionalNodeDataDTO);
        Mockito.verifyNoInteractions(errorsArchiverServiceMock);
    }

    @Test
    void givenIncorrectDataWhenProcessReceiptThenError() throws URISyntaxException {
        // Given
        ReceiptIngestionFlowFileDTO dto = podamFactory.manufacturePojo(ReceiptIngestionFlowFileDTO.class);
        dto.setIuv("IUV");
        dto.setCreditorReferenceId("IUV");
        ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = podamFactory.manufacturePojo(ReceiptWithAdditionalNodeDataDTO.class);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        workingDirectory = Path.of(new URI("file:///tmp"));

        Mockito.doNothing().when(requiredFieldsValidatorServiceMock).validateIngestionFile(ingestionFlowFile, dto);
        Mockito.when(mapperMock.map(ingestionFlowFile, dto)).thenReturn(receiptWithAdditionalNodeDataDTO);
        Mockito.when(receiptServiceMock.createReceipt(receiptWithAdditionalNodeDataDTO))
                .thenThrow(new RuntimeException("Processing error"));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        ReceiptIngestionFlowFileResult result = service.processReceipts(
                Stream.of(dto).iterator(), List.of(new CsvException("DUMMYERROR")),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        Mockito.verify(mapperMock).map(ingestionFlowFile, dto);
        Mockito.verify(receiptServiceMock).createReceipt(receiptWithAdditionalNodeDataDTO);
        verify(errorsArchiverServiceMock).writeErrors(same(workingDirectory), same(ingestionFlowFile), eq(List.of(
                new ReceiptErrorDTO(ingestionFlowFile.getFileName(), -1L, FileErrorCode.CSV_GENERIC_ERROR.name(), "Errore generico nella lettura del file: DUMMYERROR"),
                new ReceiptErrorDTO(ingestionFlowFile.getFileName(), 2L, FileErrorCode.GENERIC_ERROR.name(), "Processing error")
        )));
    }
}