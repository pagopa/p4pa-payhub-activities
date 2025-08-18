package it.gov.pagopa.payhub.activities.service.ingestionflow.sendnotification;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class SendNotificationProcessingService extends
    IngestionFlowProcessingService<SendNotificationIngestionFlowFileDTO, SendNotificationIngestionFlowFileResult, SendNotificationErrorDTO> {

  private final SendNotificationService sendNotificationService;

  public SendNotificationProcessingService(
      SendNotificationErrorArchiverService sendNotificationErrorArchiverService,
      SendNotificationService sendNotificationService,
      OrganizationService organizationService) {
    super(sendNotificationErrorArchiverService, organizationService);
    this.sendNotificationService = sendNotificationService;
  }

  @Override
  protected boolean consumeRow(long lineNumber, SendNotificationIngestionFlowFileDTO row,
      SendNotificationIngestionFlowFileResult ingestionFlowFileResult,
      List<SendNotificationErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
    return false;
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
}
