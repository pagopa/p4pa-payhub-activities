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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
    protected ErrorArchiverService<SendNotificationErrorDTO, SendNotificationIngestionFlowFileResult> getErrorsArchiverServiceMock() {
        return errorArchiverServiceMock;
    }

    @Override
    protected SendNotificationIngestionFlowFileResult startProcess(Iterator<SendNotificationIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processSendNotifications(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected SendNotificationIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        String sendNotificationId = "SENDNOTIFICATIONID" + rowNumber;
        String nav = "NAV" + rowNumber;
        String paProtocolNumber = "PAPROTOCOLNUMBER" + sequencingId;
        String senderTaxId = "SENDERTAXID" + rowNumber;

        SendNotificationIngestionFlowFileDTO dto = podamFactory.manufacturePojo(SendNotificationIngestionFlowFileDTO.class);
        dto.setPaProtocolNumber(paProtocolNumber);

        CreateNotificationRequest createNotificationRequest = buildNotificationRequest(dto.getPaProtocolNumber(), nav);
        createNotificationRequest.setSenderTaxId(senderTaxId);

        SendNotificationDTO sendNotificationDTO = podamFactory.manufacturePojo(SendNotificationDTO.class);
        sendNotificationDTO.sendNotificationId(sendNotificationId);
        sendNotificationDTO.setStatus(NotificationStatus.WAITING_FILE);
        sendNotificationDTO.setOrganizationId(ingestionFlowFile.getOrganizationId());

        CreateNotificationResponse response = podamFactory.manufacturePojo(CreateNotificationResponse.class);
        response.setSendNotificationId(sendNotificationId);

        Mockito.doReturn(createNotificationRequest)
                .when(mapperMock)
                .buildCreateNotificationRequest(dto);

        Mockito.doReturn(response)
                .when(sendNotificationServiceMock)
                .createSendNotification(createNotificationRequest);

        // useCase no payments
        if (sequencingId == 1) {
            createNotificationRequest.getRecipients().getFirst().setPayments(null);
        }
        // useCases having payments
        else {
            Payment payment = Objects.requireNonNull(createNotificationRequest.getRecipients().getFirst().getPayments()).getFirst();

            // useCase sendNotificationId not existent
            if (sequencingId == 2) {
                sendNotificationDTO = null;

                // no previous
                Mockito.doReturn(null)
                        .when(sendNotificationServiceMock)
                        .findSendNotificationByOrgIdAndNav(ingestionFlowFile.getOrganizationId(), nav);
            }
            // useCases having payments file to send
            else {
                String paymentFileSentDigest;
                String paymentFileSentName;

                // useCase no pagoPa (it will also skip the search by nav)
                if (sequencingId == 3) {
                    payment.setPagoPa(null);
                    paymentFileSentDigest = "F24DIGEST";
                    paymentFileSentName = "F24ATTACHMENT.pdf";
                } else {
                    PagoPa pagoPa = Objects.requireNonNull(payment.getPagoPa());

                    // useCase no pagopa attachment, sending just F24
                    if (sequencingId == 4) {
                        pagoPa.setAttachment(null);
                        paymentFileSentDigest = "F24DIGEST";
                        paymentFileSentName = "F24ATTACHMENT.pdf";
                    }
                    // useCase no f24 attachment, sending just pagoPa
                    else if (sequencingId == 5) {
                        payment.setF24(null);
                        paymentFileSentDigest = "PAGOPADIGEST";
                        paymentFileSentName = "PAGOPAATTACHMENT.pdf";
                    }
                    // useCase sending both
                    else {
                        paymentFileSentDigest = "PAGOPADIGEST";
                        paymentFileSentName = "PAGOPAATTACHMENT.pdf";

                        Mockito.doReturn(new StartNotificationResponse())
                                .when(sendNotificationServiceMock)
                                .startSendNotification(sendNotificationId,
                                        new LoadFileRequest(paProtocolNumber + "F24DIGEST", paProtocolNumber + "F24ATTACHMENT.pdf"));
                    }

                    Mockito.doReturn(sendNotificationDTO)
                            .when(sendNotificationServiceMock)
                            .findSendNotificationByOrgIdAndNav(ingestionFlowFile.getOrganizationId(), nav);
                }

                Mockito.doNothing()
                        .when(sendNotificationFileHandlerServiceMock)
                        .moveAllFilesToSendFolder(ingestionFlowFile.getOrganizationId(), sendNotificationId, "filePathName/" + rowNumber);

                Mockito.doReturn(new StartNotificationResponse())
                        .when(sendNotificationServiceMock)
                        .startSendNotification(sendNotificationId,
                                new LoadFileRequest(paProtocolNumber + paymentFileSentDigest, paProtocolNumber + paymentFileSentName));
            }
        }

        Mockito.doReturn(sendNotificationDTO)
                .when(sendNotificationServiceMock)
                .getSendNotification(sendNotificationId);

        // useCase having a notification to start
        if (sendNotificationDTO != null) {
            Mockito.doReturn(new StartNotificationResponse())
                    .when(sendNotificationServiceMock)
                    .startSendNotification(sendNotificationId,
                            new LoadFileRequest(paProtocolNumber + "DOCUMENTDIGEST", paProtocolNumber + "DOCUMENTATTACHMENT.pdf"));
        }

        return dto;
    }

    @Override
    protected List<Pair<SendNotificationIngestionFlowFileDTO, List<SendNotificationErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseAlreadyProcessedHavingStatusAccepted(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseAlreadyProcessedHavingStatusUploaded(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseAlreadyProcessedHavingStatusComplete(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseNotProcessed(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<SendNotificationIngestionFlowFileDTO, List<SendNotificationErrorDTO>> configureUnhappyUseCaseAlreadyProcessedHavingStatusAccepted(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseAlreadyProcessed(ingestionFlowFile, rowNumber, NotificationStatus.ACCEPTED);
    }

    private Pair<SendNotificationIngestionFlowFileDTO, List<SendNotificationErrorDTO>> configureUnhappyUseCaseAlreadyProcessedHavingStatusUploaded(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseAlreadyProcessed(ingestionFlowFile, rowNumber, NotificationStatus.UPLOADED);
    }

    private Pair<SendNotificationIngestionFlowFileDTO, List<SendNotificationErrorDTO>> configureUnhappyUseCaseAlreadyProcessedHavingStatusComplete(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseAlreadyProcessed(ingestionFlowFile, rowNumber, NotificationStatus.COMPLETE);
    }

    private Pair<SendNotificationIngestionFlowFileDTO, List<SendNotificationErrorDTO>> configureUnhappyUseCaseAlreadyProcessed(IngestionFlowFile ingestionFlowFile, long rowNumber, NotificationStatus alreadyProcessedStatus) {
        String sendNotificationId = "SENDNOTIFICATIONID" + rowNumber;
        String nav = "NAV" + rowNumber;
        String paProtocolNumber = "PAPROTOCOLNUMBERUNHAPPY" + rowNumber;
        String senderTaxId = "SENDERTAXID" + rowNumber;

        SendNotificationIngestionFlowFileDTO dto = podamFactory.manufacturePojo(SendNotificationIngestionFlowFileDTO.class);
        dto.setPaProtocolNumber(paProtocolNumber);

        CreateNotificationRequest createNotificationRequest = buildNotificationRequest(paProtocolNumber, nav);
        createNotificationRequest.setSenderTaxId(senderTaxId);

        Payment payment = Objects.requireNonNull(createNotificationRequest.getRecipients().getFirst().getPayments()).getFirst();
        PagoPa pagoPa = Objects.requireNonNull(payment.getPagoPa());
        pagoPa.setNoticeCode(nav);

        SendNotificationDTO sendNotificationDTO = podamFactory.manufacturePojo(SendNotificationDTO.class);
        sendNotificationDTO.sendNotificationId(sendNotificationId);
        sendNotificationDTO.setStatus(alreadyProcessedStatus);
        sendNotificationDTO.setOrganizationId(ingestionFlowFile.getOrganizationId());

        Mockito.doReturn(createNotificationRequest)
                .when(mapperMock)
                .buildCreateNotificationRequest(dto);

        Mockito.doReturn(sendNotificationDTO)
                .when(sendNotificationServiceMock)
                .findSendNotificationByOrgIdAndNav(ingestionFlowFile.getOrganizationId(), nav);

        List<SendNotificationErrorDTO> expectedErrors = List.of(
                SendNotificationErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("NOTIFICATION_ALREADY_PROCESSED")
                        .errorMessage(FileErrorCode.NOTIFICATION_ALREADY_PROCESSED.getMessage())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    private Pair<SendNotificationIngestionFlowFileDTO, List<SendNotificationErrorDTO>> configureUnhappyUseCaseNotProcessed(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        String paProtocolNumber = "PAPROTOCOLNUMBERUNHAPPY" + rowNumber;
        String nav = "NAV" + rowNumber;
        String senderTaxId = "SENDERTAXID" + rowNumber;

        SendNotificationIngestionFlowFileDTO dto = podamFactory.manufacturePojo(SendNotificationIngestionFlowFileDTO.class);
        dto.setPaProtocolNumber(paProtocolNumber);

        CreateNotificationRequest createNotificationRequest = buildNotificationRequest(paProtocolNumber, nav);
        createNotificationRequest.setSenderTaxId(senderTaxId);

        Payment payment = Objects.requireNonNull(createNotificationRequest.getRecipients().getFirst().getPayments()).getFirst();
        PagoPa pagoPa = Objects.requireNonNull(payment.getPagoPa());
        pagoPa.setNoticeCode(nav);

        Mockito.doReturn(createNotificationRequest)
                .when(mapperMock)
                .buildCreateNotificationRequest(dto);

        Mockito.doReturn(null)
                .when(sendNotificationServiceMock)
                .findSendNotificationByOrgIdAndNav(ingestionFlowFile.getOrganizationId(), nav);

        Mockito.doReturn(null)
                .when(sendNotificationServiceMock)
                .createSendNotification(createNotificationRequest);

        List<SendNotificationErrorDTO> expectedErrors = List.of(
                SendNotificationErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("NOTIFICATION_NOT_PROCESSED")
                        .errorMessage(FileErrorCode.NOTIFICATION_NOT_PROCESSED.getMessage())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    private CreateNotificationRequest buildNotificationRequest(String paProtocolNumber, String nav) {
        CreateNotificationRequest createNotificationRequest = new CreateNotificationRequest();
        createNotificationRequest.setOrganizationId(ingestionFlowFile.getOrganizationId());
        createNotificationRequest.setPaProtocolNumber(paProtocolNumber);
        Recipient recipient = new Recipient();
        Payment payment = getPayment(paProtocolNumber, nav);

        recipient.setPayments(List.of(payment));
        Document document = new Document();
        document.setDigest(paProtocolNumber + "DOCUMENTDIGEST");
        document.setFileName(paProtocolNumber + "DOCUMENTATTACHMENT.pdf");
        document.setContentType("content/pdf");
        createNotificationRequest.setDocuments(List.of(document));
        createNotificationRequest.setRecipients(List.of(recipient));

        return createNotificationRequest;
    }

    private Payment getPayment(String paProtocolNumber, String nav) {
        Payment payment = new Payment();

        Attachment paymentAttachment = new Attachment();
        paymentAttachment.setDigest(paProtocolNumber + "PAGOPADIGEST");
        paymentAttachment.setFileName(paProtocolNumber + "PAGOPAATTACHMENT.pdf");
        paymentAttachment.setContentType("content/pdf");
        payment.setPagoPa(new PagoPa(nav, "TAXID", true, paymentAttachment));

        Attachment f24Attachment = new Attachment();
        f24Attachment.setDigest(paProtocolNumber + "F24DIGEST");
        f24Attachment.setFileName(paProtocolNumber + "F24ATTACHMENT.pdf");
        f24Attachment.setContentType("content/pdf");
        payment.setF24(new F24Payment("titleF24", true, f24Attachment));
        return payment;
    }
}