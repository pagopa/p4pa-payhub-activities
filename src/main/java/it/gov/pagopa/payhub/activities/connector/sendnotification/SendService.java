package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.StreamEventSummaryDTO;

import java.util.List;
import java.util.Map;

public interface SendService {
  void preloadSendFile(String sendNotificationId);
  void uploadSendFile(String sendNotificationId);
  void deliveryNotification(String sendNotificationId);
  SendNotificationDTO notificationStatus(String sendNotificationId);
  SendNotificationDTO retrieveNotificationDate(String sendNotificationId);
  SendNotificationDTO retrieveNotificationByNotificationRequestId(String notificationRequestId);
  void downloadAndArchiveSendLegalFact(String notificationRequestId, LegalFactCategoryDTO legalFactCategoryDTO, String legalFactId);
  void notifySendNotificationStreamEvents(Map<String, List<StreamEventSummaryDTO>> notificationRequestIdToStreamEventsMap);
}
