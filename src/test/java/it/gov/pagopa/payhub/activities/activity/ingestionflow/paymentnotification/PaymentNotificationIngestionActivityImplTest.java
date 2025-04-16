package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentnotification;


import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification.PaymentNotificationProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationIngestionActivityImplTest {

    @Mock
    private CsvService csvServiceMock;
    @Mock
    private PaymentNotificationProcessingService paymentNotificationProcessingServiceMock;
    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;

    private PaymentNotificationIngestionActivityImpl activity;

    @TempDir
    private Path workingDir;

    @BeforeEach
    void setUp() {
        activity = new PaymentNotificationIngestionActivityImpl(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                fileArchiverServiceMock,
                csvServiceMock,
                paymentNotificationProcessingServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                fileArchiverServiceMock,
                csvServiceMock,
                paymentNotificationProcessingServiceMock
        );
    }

    @Test
    void handleRetrievedFilesSuccessfully() throws Exception {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 10L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setOrganizationId(organizationId);
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.PAYMENT_NOTIFICATION);
        Iterator<PaymentNotificationIngestionFlowFileDTO> iterator = buildPaymentNotificationIngestionFlowFileDTO();
        List<CsvException> readerExceptions = List.of();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(PaymentNotificationIngestionFlowFileDTO.class), any()))
                .thenAnswer(invocation -> {
                    BiFunction<Iterator<PaymentNotificationIngestionFlowFileDTO>, List<CsvException>, PaymentNotificationIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator, readerExceptions);
                });

        Mockito.when(paymentNotificationProcessingServiceMock.processPaymentNotification(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenReturn(buildPaymentNotificationIngestionFlowFileResult());

        // When
        PaymentNotificationIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

        // Then
        Assertions.assertEquals(
                buildPaymentNotificationIngestionFlowFileResult(),
                result);
        Mockito.verify(fileArchiverServiceMock, Mockito.times(1)).archive(ingestionFlowFileDTO);
        Assertions.assertFalse(filePath.toFile().exists());
    }


    @Test
    void givenValidIngestionFlowWhenExceptionThenThrowInvalidIngestionFileException() throws IOException {
        // Given
        Long ingestionFlowFileId = 1L;
        Long organizationId = 10L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setOrganizationId(organizationId);
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.PAYMENT_NOTIFICATION);
        Iterator<PaymentNotificationIngestionFlowFileDTO> iterator = buildPaymentNotificationIngestionFlowFileDTO();
        List<CsvException> readerExceptions = List.of();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(PaymentNotificationIngestionFlowFileDTO.class), any()))
                .thenAnswer(invocation -> {
                    BiFunction<Iterator<PaymentNotificationIngestionFlowFileDTO>, List<CsvException>, PaymentNotificationIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator, readerExceptions);
                });

        Mockito.when(paymentNotificationProcessingServiceMock.processPaymentNotification(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenThrow(new RestClientException("Error"));

        // When & Then
        assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
    }

    private PaymentNotificationIngestionFlowFileResult buildPaymentNotificationIngestionFlowFileResult() {
        return PaymentNotificationIngestionFlowFileResult.builder()
                .processedRows(20L)
                .totalRows(30L)
                .discardedFileName("dicardedFileName")
                .errorDescription("errorDescription")
                .organizationId(10L)
                .iudList(List.of(
                        "iud1",
                        "iud2"))
                .build();
    }

    private Iterator<PaymentNotificationIngestionFlowFileDTO> buildPaymentNotificationIngestionFlowFileDTO() {
        List<PaymentNotificationIngestionFlowFileDTO> paymentNotificationIngestionFlowFileDTOList = List.of(
                PaymentNotificationIngestionFlowFileDTO.builder()
                        .iud("iud1")
                        .iuv("iuv1")
                        .debtorUniqueIdentifierType("F")
                        .debtorUniqueIdentifierCode("ABC123")
                        .debtorFullName("debtor1")
                        .paymentExecutionDate(LocalDate.now())
                        .amountPaidCents(BigDecimal.valueOf(100))
                        .paCommissionCents(BigDecimal.valueOf(10))
                        .debtPositionTypeOrgCode("bptoc1")
                        .paymentType("pt1")
                        .remittanceInformation("remInfo1")
                        .transferCategory("transferCategory1")
                        .build(),
                PaymentNotificationIngestionFlowFileDTO.builder()
                        .iud("iud2")
                        .iuv("iuv2")
                        .debtorUniqueIdentifierType("F")
                        .debtorUniqueIdentifierCode("DEF456")
                        .debtorFullName("debtor2")
                        .paymentExecutionDate(LocalDate.now())
                        .amountPaidCents(BigDecimal.valueOf(200))
                        .paCommissionCents(BigDecimal.valueOf(20))
                        .debtPositionTypeOrgCode("bptoc2")
                        .paymentType("pt2")
                        .remittanceInformation("remInfo2")
                        .transferCategory("transferCategory2")
                        .build()
        );

        return paymentNotificationIngestionFlowFileDTOList.iterator();
    }

}
