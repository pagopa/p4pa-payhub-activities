package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification;


import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.paymentnotification.PaymentNotificationMapper;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationProcessingServiceTest {

  @Mock
  private PaymentNotificationErrorsArchiverService errorsArchiverServiceMock;

  @Mock
  private Path workingDirectory;
  
  @Mock
  private PaymentNotificationMapper mapperMock;

  @Mock
  private PaymentNotificationService paymentNotificationServiceMock;

  private PaymentNotificationProcessingService service;

  @BeforeEach
  void setUp() {
    service = new PaymentNotificationProcessingService(mapperMock, errorsArchiverServiceMock, paymentNotificationServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(
            mapperMock,
            errorsArchiverServiceMock,
            paymentNotificationServiceMock);
  }

  @Test
  void processPaymentNotificationWithNoErrors() {
    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    PaymentNotificationIngestionFlowFileDTO dto = mock(PaymentNotificationIngestionFlowFileDTO.class);
    PaymentNotificationDTO mappedNotification = PaymentNotificationDTO.builder()
            .iud("IUD")
            .build();
    Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
    Mockito.when(paymentNotificationServiceMock.createPaymentNotification(mappedNotification)).thenReturn(mappedNotification);

    PaymentNotificationIngestionFlowFileResult result = service.processPaymentNotification(
        Stream.of(dto).iterator(), List.of(),
            ingestionFlowFile, workingDirectory);

    Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
    Assertions.assertEquals(1L, result.getProcessedRows());
    Assertions.assertEquals(1L, result.getTotalRows());
    Assertions.assertNotNull(result.getIudList());
    Assertions.assertEquals(1, result.getIudList().size());
    Assertions.assertEquals(List.of("IUD"), result.getIudList());
    Mockito.verify(mapperMock).map(dto, ingestionFlowFile);
    Mockito.verify(paymentNotificationServiceMock).createPaymentNotification(mappedNotification);
    Mockito.verifyNoInteractions(errorsArchiverServiceMock);
  }
  
  @Test
  void givenThrowExceptionWhenProcessPaymentNotificationThenAddError() throws URISyntaxException {
    // Given
    PaymentNotificationIngestionFlowFileDTO paymentNotificationIngestionFlowFileDTO = TestUtils.getPodamFactory().manufacturePojo(PaymentNotificationIngestionFlowFileDTO.class);

    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    workingDirectory = Path.of(new URI("file:///tmp"));

    PaymentNotificationDTO mappedNotification = mock(PaymentNotificationDTO.class);
    Mockito.when(mapperMock.map(paymentNotificationIngestionFlowFileDTO, ingestionFlowFile)).thenReturn(mappedNotification);
    Mockito.when(paymentNotificationServiceMock.createPaymentNotification(mappedNotification))
        .thenThrow(new RuntimeException("Processing error"));

    Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
        .thenReturn("zipFileName.csv");

    // When
    PaymentNotificationIngestionFlowFileResult result = service.processPaymentNotification(
        Stream.of(paymentNotificationIngestionFlowFileDTO).iterator(), List.of(new CsvException("DUMMYERROR")),
        ingestionFlowFile,
        workingDirectory
    );

    // Then
    Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
    assertEquals(2, result.getTotalRows());
    assertEquals(0, result.getProcessedRows());
    assertEquals("Some rows have failed", result.getErrorDescription());
    assertEquals("zipFileName.csv", result.getDiscardedFileName());
    Assertions.assertNotNull(result.getIudList());
    Assertions.assertEquals(0, result.getIudList().size());

    verify(mapperMock).map(paymentNotificationIngestionFlowFileDTO, ingestionFlowFile);
    verify(paymentNotificationServiceMock).createPaymentNotification(mappedNotification);
    verify(errorsArchiverServiceMock).writeErrors(same(workingDirectory), same(ingestionFlowFile), eq(List.of(
            new PaymentNotificationErrorDTO(ingestionFlowFile.getFileName(), null, null, -1L, "READER_EXCEPTION", "DUMMYERROR"),
            new PaymentNotificationErrorDTO(ingestionFlowFile.getFileName(), paymentNotificationIngestionFlowFileDTO.getIuv(), paymentNotificationIngestionFlowFileDTO.getIud(), 2L, "PROCESS_EXCEPTION", "Processing error")
    )));
  }
}