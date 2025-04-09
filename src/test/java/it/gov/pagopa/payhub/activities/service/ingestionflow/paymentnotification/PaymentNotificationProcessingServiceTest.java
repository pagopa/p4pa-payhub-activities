package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification;


import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.paymentnotification.PaymentNotificationMapper;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationProcessingServiceTest {

  @Mock
  private Iterator<PaymentNotificationIngestionFlowFileDTO> iteratorMock;

  @Mock
  private IngestionFlowFile ingestionFlowFileMock;

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

  @Test
  void processPaymentNotificationWithNoErrors() {
    PaymentNotificationIngestionFlowFileDTO dto = mock(PaymentNotificationIngestionFlowFileDTO.class);
    PaymentNotificationDTO mappedNotification = mock(PaymentNotificationDTO.class);
    Mockito.when(mapperMock.map(dto, ingestionFlowFileMock)).thenReturn(mappedNotification);
    Mockito.when(paymentNotificationServiceMock.createPaymentNotification(mappedNotification)).thenReturn(mappedNotification);

    PaymentNotificationIngestionFlowFileResult result = service.processPaymentNotification(
        Stream.of(dto).iterator(),
        ingestionFlowFileMock, workingDirectory);

    Assertions.assertEquals(1L, result.getProcessedRows());
    Assertions.assertEquals(1L, result.getTotalRows());
    Assertions.assertNotNull(result.getPaymentNotificationList());
    Assertions.assertEquals(1, result.getPaymentNotificationList().size());
    Mockito.verify(mapperMock).map(dto, ingestionFlowFileMock);
    Mockito.verify(paymentNotificationServiceMock).createPaymentNotification(mappedNotification);
    Mockito.verifyNoInteractions(errorsArchiverServiceMock);
  }
  
  @Test
  void givenThrowExceptionWhenProcessPaymentNotificationThenAddError() throws URISyntaxException {
    // Given
    PaymentNotificationIngestionFlowFileDTO paymentNotificationIngestionFlowFileDTO = mock(PaymentNotificationIngestionFlowFileDTO.class);
    Mockito.when(paymentNotificationIngestionFlowFileDTO.getIud()).thenReturn("testIud");
    Mockito.when(paymentNotificationIngestionFlowFileDTO.getIuv()).thenReturn("testIuv");

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
        Stream.of(paymentNotificationIngestionFlowFileDTO).iterator(),
        ingestionFlowFile,
        workingDirectory
    );

    // Then
    assertEquals(1, result.getTotalRows());
    assertEquals(0, result.getProcessedRows());
    assertEquals("Some rows have failed", result.getErrorDescription());
    assertEquals("zipFileName.csv", result.getDiscardedFileName());

    verify(mapperMock).map(paymentNotificationIngestionFlowFileDTO, ingestionFlowFile);
    verify(paymentNotificationServiceMock).createPaymentNotification(mappedNotification);
    verify(errorsArchiverServiceMock).writeErrors(eq(workingDirectory), eq(ingestionFlowFile), any());
    verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectory, ingestionFlowFile);
  }
}