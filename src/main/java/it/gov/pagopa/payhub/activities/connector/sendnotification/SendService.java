package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.pu.sendnotification.dto.generated.NewNotificationRequestStatusResponseV24DTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

public interface SendService {
  void preloadSendFile(String sendNotificationId);
  void uploadSendFile(String sendNotificationId);
  void deliveryNotification(String sendNotificationId);
  NewNotificationRequestStatusResponseV24DTO notificationStatus(String sendNotificationId);
  SendNotificationDTO retrieveNotificationDate(String accessToken, String sendNotificationId, Long organizationId);
}
