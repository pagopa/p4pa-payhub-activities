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
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.sendnotification.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;

@Service
@Lazy
@Slf4j
public class SendNotificationProcessingService extends
        IngestionFlowProcessingService<SendNotificationIngestionFlowFileDTO, SendNotificationIngestionFlowFileResult, SendNotificationErrorDTO> {

    private final SendNotificationService sendNotificationService;
    private final SendNotificationMapper mapper;
    private final SendNotificationFileHandlerService sendNotificationFileHandlerService;
    private final FileExceptionHandlerService fileExceptionHandlerService;

    public SendNotificationProcessingService(
            @Value("${ingestion-flow-files.send-notifications.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            SendNotificationErrorArchiverService sendNotificationErrorArchiverService,
            SendNotificationService sendNotificationService,
            OrganizationService organizationService,
            FileExceptionHandlerService fileExceptionHandlerService,
            SendNotificationMapper mapper,
            SendNotificationFileHandlerService sendNotificationFileHandlerService) {
        super(maxConcurrentProcessingRows, sendNotificationErrorArchiverService, organizationService, fileExceptionHandlerService);
        this.sendNotificationService = sendNotificationService;
        this.mapper = mapper;
        this.sendNotificationFileHandlerService = sendNotificationFileHandlerService;
        this.fileExceptionHandlerService = fileExceptionHandlerService;
    }

    /**
     * Processes a stream of SendNotificationIngestionFlowFileDTO and synchronizes each installment.
     *
     * @param iterator          Stream of send notification ingestion flow file DTOs to be processed.
     * @param readerExceptions  A list which will collect the exceptions thrown during iterator processing
     * @param ingestionFlowFile Metadata of the ingestion file containing details about the ingestion process.
     * @param workingDirectory  The directory where error files will be written if processing fails.
     * @return An {@link SendNotificationIngestionFlowFileResult} containing details about the processed rows, errors, and archived files.
     */
    public SendNotificationIngestionFlowFileResult processSendNotifications(
            Iterator<SendNotificationIngestionFlowFileDTO> iterator,
            List<CsvException> readerExceptions,
            IngestionFlowFile ingestionFlowFile,
            Path workingDirectory) {
        List<SendNotificationErrorDTO> errorList = new ArrayList<>();
        SendNotificationIngestionFlowFileResult result = new SendNotificationIngestionFlowFileResult();
        process(iterator, readerExceptions, result, ingestionFlowFile, errorList, workingDirectory);
        result.setFileVersion(ingestionFlowFile.getFileVersion());
        result.setOrganizationId(ingestionFlowFile.getOrganizationId());
        return result;
    }

    @Override
    protected String getSequencingId(SendNotificationIngestionFlowFileDTO row) {
        return row.getPaProtocolNumber();
    }

    @Override
    protected List<SendNotificationErrorDTO> consumeRow(long lineNumber, SendNotificationIngestionFlowFileDTO row,
                                 SendNotificationIngestionFlowFileResult ingestionFlowFileResult,
                                 IngestionFlowFile ingestionFlowFile) {

        try {
            CreateNotificationRequest createNotificationRequest = mapper.buildCreateNotificationRequest(row);

            // check if exists a send notification with status UPLOADED/COMPLETE/ACCEPTED and if exists skip row
            if (createNotificationRequest.getRecipients().getFirst().getPayments() != null
                    && createNotificationRequest.getRecipients().getFirst().getPayments().getFirst().getPagoPa() != null &&
                    checkSendNotificationAlreadyExists(createNotificationRequest.getOrganizationId(), createNotificationRequest.getRecipients().getFirst().getPayments())) {
                return List.of(buildErrorDto(ingestionFlowFile.getFileName(), lineNumber, FileErrorCode.NOTIFICATION_ALREADY_PROCESSED.name(),
                        FileErrorCode.NOTIFICATION_ALREADY_PROCESSED.getMessage()));
            }

            CreateNotificationResponse createResponse = sendNotificationService.createSendNotification(createNotificationRequest);
            if (createResponse != null) {
                SendNotificationDTO sendNotificationDTO = sendNotificationService.getSendNotification(createResponse.getSendNotificationId());

                if (sendNotificationDTO != null) {
                    createNotificationRequest.getRecipients().stream()
                            .filter(recipient -> recipient.getPayments() != null)
                            .flatMap(recipient -> recipient.getPayments().stream())
                            .forEach(payment -> {
                                sendNotificationFileHandlerService.moveAllFilesToSendFolder(
                                        sendNotificationDTO.getOrganizationId(),
                                        sendNotificationDTO.getSendNotificationId(),
                                        ingestionFlowFile.getFilePathName() + "/" + lineNumber
                                );

                                if (payment.getPagoPa() != null && !Objects.isNull(payment.getPagoPa().getAttachment())) {
                                    sendNotificationService.startSendNotification(sendNotificationDTO.getSendNotificationId(),
                                            new LoadFileRequest(payment.getPagoPa().getAttachment().getDigest(),
                                                    payment.getPagoPa().getAttachment().getFileName()));
                                }
                                if (payment.getF24() != null) {
                                    sendNotificationService.startSendNotification(sendNotificationDTO.getSendNotificationId(),
                                            new LoadFileRequest(payment.getF24().getMetadataAttachment().getDigest(),
                                                    payment.getF24().getMetadataAttachment().getFileName()));
                                }
                            });

                    createNotificationRequest.getDocuments()
                            .stream()
                            .filter(Objects::nonNull)
                            .forEach(doc -> sendNotificationService.startSendNotification(sendNotificationDTO.getSendNotificationId(),
                                    new LoadFileRequest(doc.getDigest(), doc.getFileName())));
                }
                return Collections.emptyList();
            }
            return List.of(buildErrorDto(ingestionFlowFile.getFileName(), lineNumber,
                    FileErrorCode.NOTIFICATION_NOT_PROCESSED.name(),
                    FileErrorCode.NOTIFICATION_NOT_PROCESSED.getMessage()));
        } catch (Exception e) {
            log.error("Error processing send notification: {}", e.getMessage());
            FileExceptionHandlerService.ErrorDetails errorDetails = fileExceptionHandlerService.mapExceptionToErrorCodeAndMessage(e.getMessage());
            SendNotificationErrorDTO error = SendNotificationErrorDTO.builder()
                    .fileName(ingestionFlowFile.getFileName())
                    .rowNumber(lineNumber)
                    .errorCode(errorDetails.getErrorCode())
                    .errorMessage(errorDetails.getErrorMessage())
                    .build();

            return List.of(error);
        }
    }

    @Override
    protected SendNotificationErrorDTO buildErrorDto(String fileName, long lineNumber,
                                                     String errorCode, String message) {
        return SendNotificationErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }

    private boolean checkSendNotificationAlreadyExists(Long organizationId, List<Payment> payments) {
        return payments.stream()
                .map(payment -> {
                    try {
                        return sendNotificationService.findSendNotificationByOrgIdAndNav(organizationId, payment.getPagoPa().getNoticeCode());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .anyMatch(notificationDTO ->
                        notificationDTO.getStatus().equals(NotificationStatus.UPLOADED) ||
                                notificationDTO.getStatus().equals(NotificationStatus.COMPLETE) ||
                                notificationDTO.getStatus().equals(NotificationStatus.ACCEPTED)
                );
    }
}
