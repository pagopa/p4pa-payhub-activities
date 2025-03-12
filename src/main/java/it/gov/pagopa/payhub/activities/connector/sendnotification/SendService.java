package it.gov.pagopa.payhub.activities.connector.sendnotification;

public interface SendService {
  void preloadSendFile(String sendNotificationId);
  void uploadSendFile(String sendNotificationId);
}
