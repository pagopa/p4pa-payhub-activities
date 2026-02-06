package it.gov.pagopa.payhub.activities.service.ingestionflow.sendnotification;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification.SendNotificationMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.sendnotification.dto.generated.*;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendNotificationProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<SendNotificationIngestionFlowFileDTO, SendNotificationIngestionFlowFileResult, SendNotificationErrorDTO> {

    @Mock
    private SendNotificationErrorArchiverService errorArchiverServiceMock;
    @Mock
    private SendNotificationService sendNotificationServiceMock;
    @Mock
    private SendNotificationMapper mapperMock;
    @Mock
    private SendNotificationFileHandlerService sendNotificationFileHandlerServiceMock;

    private SendNotificationProcessingService serviceSpy;

    protected SendNotificationProcessingServiceTest() {
        super(false);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new SendNotificationProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                errorArchiverServiceMock,
                sendNotificationServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService,
                mapperMock,
                sendNotificationFileHandlerServiceMock
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                errorArchiverServiceMock,
                sendNotificationServiceMock,
                organizationServiceMock,
                mapperMock,
                sendNotificationFileHandlerServiceMock
        );
    }

    @Override
    protected SendNotificationProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<SendNotificationErrorDTO> getErrorsArchiverServiceMock() {
        return errorArchiverServiceMock;
    }

    @Override
    protected SendNotificationIngestionFlowFileResult startProcess(Iterator<SendNotificationIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processSendNotifications(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected SendNotificationIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        SendNotificationIngestionFlowFileDTO dto = podamFactory.manufacturePojo(SendNotificationIngestionFlowFileDTO.class);
        dto.setPaProtocolNumber("PAPROTOCOLNUMBER" + sequencingId);

        String sendNotificationId = UUID.nameUUIDFromBytes(dto.getAddress().getBytes()).toString();
        String nav = UUID.nameUUIDFromBytes(sendNotificationId.getBytes()).toString();
        String senderTaxId = UUID.nameUUIDFromBytes(nav.getBytes()).toString();

        CreateNotificationRequest createNotificationRequest = buildNotificationRequest();
        createNotificationRequest.setPaProtocolNumber(dto.getPaProtocolNumber());
        createNotificationRequest.senderTaxId(senderTaxId);

        CreateNotificationResponse response = podamFactory.manufacturePojo(CreateNotificationResponse.class);
        response.setSendNotificationId(sendNotificationId);

        SendNotificationDTO sendNotificationDTO = podamFactory.manufacturePojo(SendNotificationDTO.class);
        sendNotificationDTO.sendNotificationId(sendNotificationId);
        sendNotificationDTO.setStatus(NotificationStatus.WAITING_FILE);
        sendNotificationDTO.setOrganizationId(ingestionFlowFile.getOrganizationId());

        Payment payment = Objects.requireNonNull(createNotificationRequest.getRecipients().getFirst().getPayments()).getFirst();
        PagoPa pagoPa = Objects.requireNonNull(payment.getPagoPa());
        pagoPa.setNoticeCode(nav);

        if (sequencingId == 1) {
            pagoPa.setAttachment(null);
        } else if (sequencingId == 2) {
            payment.setF24(null);
        }

        Mockito.doReturn(createNotificationRequest)
                .when(mapperMock)
                .buildCreateNotificationRequest(dto);

        Mockito.doReturn(sendNotificationDTO)
                .when(sendNotificationServiceMock)
                .findSendNotificationByOrgIdAndNav(ingestionFlowFile.getOrganizationId(), nav);

        Mockito.doNothing()
                .when(sendNotificationFileHandlerServiceMock)
                .moveAllFilesToSendFolder(ingestionFlowFile.getOrganizationId(), sendNotificationId, "filePathName/" + rowNumber);

        Mockito.doReturn(new StartNotificationResponse())
                .when(sendNotificationServiceMock)
                .startSendNotification(sendNotificationId,
                        new LoadFileRequest("DIGEST", "ATTACHMENT.pdf"));

        Mockito.doReturn(response)
                .when(sendNotificationServiceMock)
                .createSendNotification(createNotificationRequest);

        Mockito.doReturn(sendNotificationDTO)
                .when(sendNotificationServiceMock)
                .getSendNotification(sendNotificationId);

        return dto;
    }

    @Override
    protected List<Pair<SendNotificationIngestionFlowFileDTO, List<SendNotificationErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of();
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

        Mockito.when(errorArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        SendNotificationIngestionFlowFileResult result = serviceSpy.processSendNotifications(
                Stream.of(sendNotificationIngestionFlowFileDTO).iterator(), List.of(new CsvException("DUMMYERROR")),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(2, result.getTotalRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        verify(errorArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                new SendNotificationErrorDTO(ingestionFlowFile.getFileName(), -1L, FileErrorCode.CSV_GENERIC_ERROR.name(), "Errore generico nella lettura del file: DUMMYERROR"),
                new SendNotificationErrorDTO(ingestionFlowFile.getFileName(), 2L, FileErrorCode.PROCESSING_ERROR.name(), "Error when create notification")
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

        Mockito.when(errorArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        SendNotificationIngestionFlowFileResult result = serviceSpy.processSendNotifications(
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

        verify(errorArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                new SendNotificationErrorDTO(ingestionFlowFile.getFileName(), 1L, FileErrorCode.NOTIFICATION_ALREADY_PROCESSED.name(),
                        FileErrorCode.NOTIFICATION_ALREADY_PROCESSED.getMessage())
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

        Mockito.when(errorArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        SendNotificationIngestionFlowFileResult result = serviceSpy.processSendNotifications(
                Stream.of(sendNotificationIngestionFlowFileDTO).iterator(), List.of(),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        verify(errorArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                new SendNotificationErrorDTO(ingestionFlowFile.getFileName(), 1L,
                        FileErrorCode.NOTIFICATION_NOT_PROCESSED.name(), FileErrorCode.NOTIFICATION_NOT_PROCESSED.getMessage())
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
        SendNotificationIngestionFlowFileResult result = serviceSpy.processSendNotifications(
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
        SendNotificationIngestionFlowFileResult result = serviceSpy.processSendNotifications(
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
        createNotificationRequest.setOrganizationId(ingestionFlowFile.getOrganizationId());
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