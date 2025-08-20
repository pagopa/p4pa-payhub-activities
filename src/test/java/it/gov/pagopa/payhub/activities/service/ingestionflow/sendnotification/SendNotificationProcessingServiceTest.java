package it.gov.pagopa.payhub.activities.service.ingestionflow.sendnotification;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.*;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification.SendNotificationMapper;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationResponse;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
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

  private SendNotificationProcessingService service;

  @BeforeEach
  void setUp(){
    service = new SendNotificationProcessingService(
        sendNotificationErrorArchiverServiceMock,
        sendNotificationServiceMock,
        organizationServiceMock,
        sendServiceMock,
        mapperMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(
        sendNotificationErrorArchiverServiceMock,
        mapperMock,
        sendNotificationErrorArchiverServiceMock,
        sendNotificationServiceMock);
  }

  @Test
  void whenProcessSendNotificationThenSuccess(){
    // Given
    SendNotificationIngestionFlowFileDTO sendNotificationIngestionFlowFileDTO = new SendNotificationIngestionFlowFileDTO();
    CreateNotificationRequest createNotificationRequest = new CreateNotificationRequest();
    CreateNotificationResponse response = new CreateNotificationResponse();
    response.setSendNotificationId("NOTIFICATIONID");
    SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
    sendNotificationDTO.sendNotificationId("NOTIFICATIONID");

    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

    Mockito.when(mapperMock.buildCreateNotificationRequest(sendNotificationIngestionFlowFileDTO))
        .thenReturn(createNotificationRequest);

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
}