package it.gov.pagopa.payhub.activities.service.ingestionflow.sendnotification;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification.SendNotificationMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.sendnotification.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendNotificationProcessingServiceTest {

    @Mock
    private SendNotificationErrorArchiverService sendNotificationErrorArchiverServiceMock;
    @Mock
    private SendNotificationService sendNotificationServiceMock;
    @Mock
    private SendNotificationMapper mapperMock;
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private SendNotificationFileHandlerService sendNotificationFileHandlerServiceMock;
    @Mock
    private FileExceptionHandlerService fileExceptionHandlerServiceMock;

    private SendNotificationProcessingService service;

    private static final String PROCESS_EXCEPTION = "PROCESS_EXCEPTION";

    private final FileExceptionHandlerService.CsvErrorDetails csvErrorDetails =
            new FileExceptionHandlerService.CsvErrorDetails(FileErrorCode.CSV_GENERIC_ERROR.name(), "Errore");

    @BeforeEach
    void setUp() {
        service = new SendNotificationProcessingService(
                sendNotificationErrorArchiverServiceMock,
                sendNotificationServiceMock,
                organizationServiceMock,
                fileExceptionHandlerServiceMock,
                mapperMock,
                sendNotificationFileHandlerServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                sendNotificationErrorArchiverServiceMock,
                mapperMock,
                sendNotificationErrorArchiverServiceMock,
                sendNotificationServiceMock,
                sendNotificationFileHandlerServiceMock);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    void whenProcessSendNotificationThenSuccess(String index) {
        // Given
        SendNotificationIngestionFlowFileDTO sendNotificationIngestionFlowFileDTO = new SendNotificationIngestionFlowFileDTO();

        Long organizationId = 1L;
        String sendNotificationId = "NOTIFICATIONID";
        String nav = "NAV";

        CreateNotificationRequest createNotificationRequest = buildNotificationRequest();

        CreateNotificationResponse response = new CreateNotificationResponse();
        response.setSendNotificationId(sendNotificationId);

        SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
        sendNotificationDTO.sendNotificationId(sendNotificationId);
        sendNotificationDTO.setStatus(NotificationStatus.WAITING_FILE);
        sendNotificationDTO.setOrganizationId(organizationId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

        if (Objects.equals(index, "1")) {
            Objects.requireNonNull(createNotificationRequest.getRecipients().getFirst().getPayments()).getFirst().getPagoPa().setAttachment(null);
        } else if (Objects.equals(index, "2")) {
            Objects.requireNonNull(createNotificationRequest.getRecipients().getFirst().getPayments()).getFirst().setF24(null);
        }

        Mockito.when(mapperMock.buildCreateNotificationRequest(sendNotificationIngestionFlowFileDTO))
                .thenReturn(createNotificationRequest);

        Mockito.when(sendNotificationServiceMock.findSendNotificationByOrgIdAndNav(organizationId, nav))
                .thenReturn(sendNotificationDTO);

        Mockito.doNothing().when(sendNotificationFileHandlerServiceMock)
                .moveAllFilesToSendFolder(organizationId, sendNotificationId, "filePathName/1");

        Mockito.when(sendNotificationServiceMock.startSendNotification(sendNotificationId,
                new LoadFileRequest("DIGEST", "ATTACHMENT.pdf"))).thenReturn(new StartNotificationResponse());

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

        CreateNotificationRequest createNotificationRequest = buildNotificationRequest();

        SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
        sendNotificationDTO.sendNotificationId("NOTIFICATIONID");
        sendNotificationDTO.setStatus(NotificationStatus.WAITING_FILE);
        sendNotificationDTO.setOrganizationId(organizationId);
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

        Path workingDirectory = Path.of(new URI("file:///tmp"));

        Mockito.when(mapperMock.buildCreateNotificationRequest(sendNotificationIngestionFlowFileDTO))
                .thenReturn(createNotificationRequest);

        Mockito.doThrow(new RestClientException("Notification Not Found"))
                .when(sendNotificationServiceMock).findSendNotificationByOrgIdAndNav(organizationId, nav);

        Mockito.doThrow(new RestClientException("Error when create notification"))
                .when(sendNotificationServiceMock).createSendNotification(createNotificationRequest);

        Mockito.when(sendNotificationErrorArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        CsvException exception = new CsvException("DUMMYERROR");
        Mockito.when(fileExceptionHandlerServiceMock.mapCsvExceptionToErrorCodeAndMessage(exception))
                .thenReturn(csvErrorDetails);

        // When
        SendNotificationIngestionFlowFileResult result = service.processSendNotifications(
                Stream.of(sendNotificationIngestionFlowFileDTO).iterator(), List.of(exception),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(2, result.getTotalRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        verify(sendNotificationErrorArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                new SendNotificationErrorDTO(ingestionFlowFile.getFileName(), -1L, "CSV_GENERIC_ERROR", "Errore"),
                new SendNotificationErrorDTO(ingestionFlowFile.getFileName(), 2L, PROCESS_EXCEPTION, "Error when create notification")
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    void whenProcessSendNotificationThenSendNotificationAlreadyExist(String index) throws URISyntaxException {
        // Given
        SendNotificationIngestionFlowFileDTO sendNotificationIngestionFlowFileDTO = new SendNotificationIngestionFlowFileDTO();

        Long organizationId = 1L;
        String sendNotificationId = "NOTIFICATIONID";
        String nav = "NAV";
        Path workingDirectory = Path.of(new URI("file:///tmp"));

        CreateNotificationRequest createNotificationRequest = buildNotificationRequest();

        SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
        sendNotificationDTO.sendNotificationId(sendNotificationId);
        sendNotificationDTO.setStatus(NotificationStatus.ACCEPTED);
        if (Objects.equals(index, "1")) {
            sendNotificationDTO.setStatus(NotificationStatus.UPLOADED);
        } else if (Objects.equals(index, "2")) {
            sendNotificationDTO.setStatus(NotificationStatus.COMPLETE);
        }
        sendNotificationDTO.setOrganizationId(organizationId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

        Mockito.when(mapperMock.buildCreateNotificationRequest(sendNotificationIngestionFlowFileDTO))
                .thenReturn(createNotificationRequest);

        Mockito.when(sendNotificationServiceMock.findSendNotificationByOrgIdAndNav(organizationId, nav))
                .thenReturn(sendNotificationDTO);

        Mockito.when(sendNotificationErrorArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        SendNotificationIngestionFlowFileResult result = service.processSendNotifications(
                Stream.of(sendNotificationIngestionFlowFileDTO).iterator(), List.of(),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        assertSame(ingestionFlowFile.getFileVersion(), result.getFileVersion());
        assertEquals(0, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertEquals(1, result.getOrganizationId());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        verify(sendNotificationErrorArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                new SendNotificationErrorDTO(ingestionFlowFile.getFileName(), 1L, PROCESS_EXCEPTION, "Row not processed, notification already exists")
        ));
    }

    @Test
    void whenProcessSendNotificationThenCreateResponseNull() throws URISyntaxException {
        // Given
        SendNotificationIngestionFlowFileDTO sendNotificationIngestionFlowFileDTO = new SendNotificationIngestionFlowFileDTO();
        Path workingDirectory = Path.of(new URI("file:///tmp"));
        CreateNotificationRequest createNotificationRequest = buildNotificationRequest();

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

        Mockito.when(mapperMock.buildCreateNotificationRequest(sendNotificationIngestionFlowFileDTO))
                .thenReturn(createNotificationRequest);

        Mockito.when(sendNotificationServiceMock.findSendNotificationByOrgIdAndNav(
                        createNotificationRequest.getOrganizationId(), "NAV"))
                .thenReturn(null);

        Mockito.when(sendNotificationServiceMock.createSendNotification(createNotificationRequest))
                .thenReturn(null);

        Mockito.when(sendNotificationErrorArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        SendNotificationIngestionFlowFileResult result = service.processSendNotifications(
                Stream.of(sendNotificationIngestionFlowFileDTO).iterator(), List.of(),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        verify(sendNotificationErrorArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                new SendNotificationErrorDTO(ingestionFlowFile.getFileName(), 1L, PROCESS_EXCEPTION, "Error while create notification")
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2"})
    void givenSendNotificationDTONullWhenProcessSendNotificationThenSkipMoveFiles(String index) {
        // given
        SendNotificationIngestionFlowFileDTO sendNotificationIngestionFlowFileDTO = new SendNotificationIngestionFlowFileDTO();
        CreateNotificationRequest createNotificationRequest = buildNotificationRequest();

        Long organizationId = 1L;
        String sendNotificationId = "NOTIFICATIONID";
        String nav = "NAV";

        CreateNotificationRequest request = buildNotificationRequest();
        if (Objects.equals(index, "1")) {
            request.getRecipients().getFirst().setPayments(null);
        } else {
            request.getRecipients().getFirst().getPayments().getFirst().setPagoPa(null);
        }
        CreateNotificationResponse response = new CreateNotificationResponse();
        response.setSendNotificationId(sendNotificationId);

        SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
        sendNotificationDTO.sendNotificationId(sendNotificationId);
        sendNotificationDTO.setStatus(NotificationStatus.WAITING_FILE);
        sendNotificationDTO.setOrganizationId(organizationId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

        Mockito.when(mapperMock.buildCreateNotificationRequest(sendNotificationIngestionFlowFileDTO))
                .thenReturn(createNotificationRequest);

        Mockito.when(sendNotificationServiceMock.findSendNotificationByOrgIdAndNav(organizationId, nav))
                .thenReturn(sendNotificationDTO);

        Mockito.when(sendNotificationServiceMock.createSendNotification(createNotificationRequest))
                .thenReturn(response);

        Mockito.when(sendNotificationServiceMock.getSendNotification(sendNotificationId))
                .thenReturn(null);

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

    @ParameterizedTest
    @ValueSource(strings = {"1", "2"})
    void givenNullPaymentsWhenProcessSendNotificationThenSuccess(String index) {
        // Given
        SendNotificationIngestionFlowFileDTO sendNotificationIngestionFlowFileDTO = new SendNotificationIngestionFlowFileDTO();

        Long organizationId = 1L;
        String sendNotificationId = "NOTIFICATIONID";

        CreateNotificationRequest createNotificationRequest = buildNotificationRequest();
        if (Objects.equals(index, "1")) {
            createNotificationRequest.getRecipients().getFirst().setPayments(null);
        } else if (Objects.equals(index, "2")) {
            Objects.requireNonNull(createNotificationRequest.getRecipients().getFirst().getPayments()).getFirst().setPagoPa(null);
            Mockito.doNothing().when(sendNotificationFileHandlerServiceMock)
                    .moveAllFilesToSendFolder(organizationId, sendNotificationId, "filePathName/1");
        }

        CreateNotificationResponse response = new CreateNotificationResponse();
        response.setSendNotificationId(sendNotificationId);

        SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
        sendNotificationDTO.sendNotificationId(sendNotificationId);
        sendNotificationDTO.setStatus(NotificationStatus.WAITING_FILE);
        sendNotificationDTO.setOrganizationId(organizationId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

        Mockito.when(mapperMock.buildCreateNotificationRequest(sendNotificationIngestionFlowFileDTO))
                .thenReturn(createNotificationRequest);

        Mockito.when(sendNotificationServiceMock.startSendNotification(sendNotificationId,
                new LoadFileRequest("DIGEST", "ATTACHMENT.pdf"))).thenReturn(new StartNotificationResponse());

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

    private CreateNotificationRequest buildNotificationRequest() {
        CreateNotificationRequest createNotificationRequest = new CreateNotificationRequest();
        createNotificationRequest.setOrganizationId(1L);
        Recipient recipient = new Recipient();
        Payment payment = new Payment();
        Attachment attachment = new Attachment();
        attachment.setDigest("DIGEST");
        attachment.setFileName("ATTACHMENT.pdf");
        attachment.setContentType("content/pdf");
        payment.setPagoPa(new PagoPa("NAV", "TAXID", true, attachment));
        payment.setF24(new F24Payment("titleF24", true, attachment));
        recipient.setPayments(List.of(payment));
        Document document = new Document();
        document.setDigest("DIGEST");
        document.setFileName("ATTACHMENT.pdf");
        document.setContentType("content/pdf");
        createNotificationRequest.setDocuments(List.of(document));
        createNotificationRequest.setRecipients(List.of(recipient));

        return createNotificationRequest;
    }
}