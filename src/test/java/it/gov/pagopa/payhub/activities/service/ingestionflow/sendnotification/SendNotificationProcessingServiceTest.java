package it.gov.pagopa.payhub.activities.service.ingestionflow.sendnotification;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification.SendNotificationMapper;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationResponse;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationStatus;
import it.gov.pagopa.pu.sendnotification.dto.generated.PagoPa;
import it.gov.pagopa.pu.sendnotification.dto.generated.Payment;
import it.gov.pagopa.pu.sendnotification.dto.generated.Recipient;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class SendNotificationProcessingServiceTest {

  @Mock
  private SendNotificationErrorArchiverService sendNotificationErrorArchiverServiceMock;
  @Mock
  private SendNotificationService sendNotificationServiceMock;
  @Mock
  private SendService sendServiceMock;
  @Mock
  private SendNotificationMapper mapperMock;
  @Mock
  private OrganizationService organizationServiceMock;
  @Mock
  private SendNotificationFileHandlerService sendNotificationFileHandlerServiceMock;

  private SendNotificationProcessingService service;

  @BeforeEach
  void setUp(){
    service = new SendNotificationProcessingService(
        sendNotificationErrorArchiverServiceMock,
        sendNotificationServiceMock,
        organizationServiceMock,
        sendServiceMock,
        mapperMock,
        sendNotificationFileHandlerServiceMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(
        sendNotificationErrorArchiverServiceMock,
        mapperMock,
        sendNotificationErrorArchiverServiceMock,
        sendNotificationServiceMock,
        sendNotificationFileHandlerServiceMock);
  }

  @Test
  void whenProcessSendNotificationThenSuccess(){
    // Given
    SendNotificationIngestionFlowFileDTO sendNotificationIngestionFlowFileDTO = new SendNotificationIngestionFlowFileDTO();

    Long organizationId = 1L;
    String sendNotificationId = "NOTIFICATIONID";
    String nav = "NAV";

    CreateNotificationRequest createNotificationRequest = new CreateNotificationRequest();
    createNotificationRequest.setOrganizationId(organizationId);
    Recipient recipient = new Recipient();
    Payment payment = new Payment();
    payment.setPagoPa(new PagoPa(nav,"TAXID",true, null));
    recipient.setPayments(List.of(payment));
    createNotificationRequest.setRecipients(List.of(recipient));

    CreateNotificationResponse response = new CreateNotificationResponse();
    response.setSendNotificationId(sendNotificationId);

    SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
    sendNotificationDTO.sendNotificationId(sendNotificationId);
    sendNotificationDTO.setStatus(NotificationStatus.WAITING_FILE);
    sendNotificationDTO.setOrganizationId(organizationId);

    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

    Mockito.when(mapperMock.buildCreateNotificationRequest(sendNotificationIngestionFlowFileDTO))
        .thenReturn(createNotificationRequest);

    Mockito.when(sendNotificationServiceMock.findSendNotificationByOrgIdAndNav(organizationId,nav))
        .thenReturn(sendNotificationDTO);

    Mockito.doNothing().when(sendNotificationFileHandlerServiceMock)
        .moveAllFilesToSendFolder(organizationId, sendNotificationId, "filePathName/NAV");

    Mockito.when(sendNotificationServiceMock.createSendNotification(createNotificationRequest))
        .thenReturn(response);

    Mockito.when(sendNotificationServiceMock.getSendNotification("NOTIFICATIONID"))
        .thenReturn(sendNotificationDTO);

    // When
    SendNotificationIngestionFlowFileResult result = service.processSendNotifications(
        Stream.of(sendNotificationIngestionFlowFileDTO).iterator(), List.of(),
        ingestionFlowFile,
        Path.of("/tmp")
    );

    // Then
    assertSame(ingestionFlowFile.getFileVersion(), result.getFileVersion());
    assertEquals(1, result.getProcessedRows());
    assertEquals(1, result.getTotalRows());
    assertEquals(1, result.getOrganizationId());
    assertNull(result.getErrorDescription());
    assertNull(result.getDiscardedFileName());
  }

  @Test
  void givenThrowExceptionWhenSendNotificationThenAddError() throws URISyntaxException {
    // Given
    SendNotificationIngestionFlowFileDTO sendNotificationIngestionFlowFileDTO = new SendNotificationIngestionFlowFileDTO();
    Long organizationId = 1L;
    String nav = "NAV";

    CreateNotificationRequest createNotificationRequest = new CreateNotificationRequest();
    createNotificationRequest.setOrganizationId(organizationId);
    Recipient recipient = new Recipient();
    Payment payment = new Payment();
    payment.setPagoPa(new PagoPa(nav,"TAXID",true, null));
    recipient.setPayments(List.of(payment));
    createNotificationRequest.setRecipients(List.of(recipient));

    SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
    sendNotificationDTO.sendNotificationId("NOTIFICATIONID");
    sendNotificationDTO.setStatus(NotificationStatus.WAITING_FILE);
    sendNotificationDTO.setOrganizationId(organizationId);
    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

    Path workingDirectory = Path.of(new URI("file:///tmp"));

    Mockito.when(mapperMock.buildCreateNotificationRequest(sendNotificationIngestionFlowFileDTO))
        .thenReturn(createNotificationRequest);

    Mockito.when(sendNotificationServiceMock.findSendNotificationByOrgIdAndNav(organizationId,nav))
        .thenReturn(sendNotificationDTO);

    Mockito.doThrow(new RestClientException("Error when create notification"))
        .when(sendNotificationServiceMock).createSendNotification(createNotificationRequest);

    Mockito.when(sendNotificationErrorArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
        .thenReturn("zipFileName.csv");

    // When
    SendNotificationIngestionFlowFileResult result = service.processSendNotifications(
        Stream.of(sendNotificationIngestionFlowFileDTO).iterator(), List.of(new CsvException("DUMMYERROR")),
        ingestionFlowFile,
        workingDirectory
    );

    // Then
    assertEquals(0, result.getProcessedRows());
    assertEquals(2, result.getTotalRows());
    assertEquals("Some rows have failed", result.getErrorDescription());
    assertEquals("zipFileName.csv", result.getDiscardedFileName());

    verify(sendNotificationErrorArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
        new SendNotificationErrorDTO(ingestionFlowFile.getFileName(), -1L, "READER_EXCEPTION", "DUMMYERROR"),
        new SendNotificationErrorDTO(ingestionFlowFile.getFileName(), 2L, "PROCESS_EXCEPTION", "Error when create notification")
    ));
  }
}