package it.gov.pagopa.payhub.activities.service.ingestionflow.sendnotification;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification.SendNotificationMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationResponse;
import it.gov.pagopa.pu.sendnotification.dto.generated.LoadFileRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationStatus;
import it.gov.pagopa.pu.sendnotification.dto.generated.Payment;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class SendNotificationProcessingService extends
    IngestionFlowProcessingService<SendNotificationIngestionFlowFileDTO, SendNotificationIngestionFlowFileResult, SendNotificationErrorDTO> {

  private final SendNotificationService sendNotificationService;
  private final SendService sendService;
  private final SendNotificationMapper mapper;
  private final SendNotificationFileHandlerService sendNotificationFileHandlerService;

  public SendNotificationProcessingService(
      SendNotificationErrorArchiverService sendNotificationErrorArchiverService,
      SendNotificationService sendNotificationService,
      OrganizationService organizationService, SendService sendService,
      SendNotificationMapper mapper,
      SendNotificationFileHandlerService sendNotificationFileHandlerService) {
    super(sendNotificationErrorArchiverService, organizationService);
    this.sendNotificationService = sendNotificationService;
    this.sendService = sendService;
    this.mapper = mapper;
    this.sendNotificationFileHandlerService = sendNotificationFileHandlerService;
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
  protected boolean consumeRow(long lineNumber, SendNotificationIngestionFlowFileDTO row,
      SendNotificationIngestionFlowFileResult ingestionFlowFileResult,
      List<SendNotificationErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {

      try {
        CreateNotificationRequest createNotificationRequest = mapper.buildCreateNotificationRequest(row);

        // check if exists a send notification with status UPLOADED/COMPLETE/ACCEPTED and if exists skip row
        if (createNotificationRequest.getRecipients().getFirst().getPayments() != null
            && checkSendNotificationAlreadyExists(createNotificationRequest.getOrganizationId(), createNotificationRequest.getRecipients().getFirst().getPayments())) {
          return false;
        }

        CreateNotificationResponse createResponse =  sendNotificationService.createSendNotification(createNotificationRequest);
        if(createResponse!=null)
        {
          SendNotificationDTO sendNotificationDTO = sendNotificationService.getSendNotification(createResponse.getSendNotificationId());

          if(sendNotificationDTO!=null) {
            createNotificationRequest.getRecipients().stream()
                .filter(recipient -> recipient.getPayments() != null)
                .flatMap(recipient -> recipient.getPayments().stream())
                .forEach(payment -> {
                    sendNotificationFileHandlerService.moveAllFilesToSendFolder(
                        sendNotificationDTO.getOrganizationId(),
                        sendNotificationDTO.getSendNotificationId(),
                        ingestionFlowFile.getFilePathName() + "/" + payment.getPagoPa().getNoticeCode()
                    );

                  if(!Objects.isNull(payment.getPagoPa().getAttachment())) {
                    sendNotificationService.startSendNotification(sendNotificationDTO.getSendNotificationId(),
                        new LoadFileRequest(payment.getPagoPa().getAttachment().getDigest(),
                            payment.getPagoPa().getAttachment().getFileName()));
                  }
                });

              createNotificationRequest.getDocuments()
                  .stream()
                  .filter(Objects::nonNull)
                  .forEach(doc -> {
                    sendNotificationService.startSendNotification(sendNotificationDTO.getSendNotificationId(),
                        new LoadFileRequest(doc.getDigest(), doc.getFileName()));
                  });
          }
        }
        return true;
      } catch (Exception e) {
        log.error("Error processing send notification: {}", e.getMessage());
        SendNotificationErrorDTO error = SendNotificationErrorDTO.builder()
          .fileName(ingestionFlowFile.getFileName())
          .rowNumber(lineNumber)
          .errorCode("PROCESS_EXCEPTION")
          .errorMessage(e.getMessage())
          .build();

        errorList.add(error);
        log.info("Current error list size after handleProcessingError: {}", errorList.size());
        return false;
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
          try{
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
