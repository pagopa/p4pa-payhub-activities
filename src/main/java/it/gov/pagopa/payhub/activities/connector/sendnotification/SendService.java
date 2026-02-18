package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

public interface SendService {
  void preloadSendFile(String sendNotificationId);
  void uploadSendFile(String sendNotificationId);
  void deliveryNotification(String sendNotificationId);
  SendNotificationDTO notificationStatus(String sendNotificationId);
  SendNotificationDTO retrieveNotificationDate(String sendNotificationId);
  SendNotificationDTO retrieveNotificationByNotificationRequestId(String notificationRequestId);
  void downloadAndArchiveSendLegalFact(String notificationRequestId, LegalFactCategoryDTO legalFactCategoryDTO, String legalFactId);
}
