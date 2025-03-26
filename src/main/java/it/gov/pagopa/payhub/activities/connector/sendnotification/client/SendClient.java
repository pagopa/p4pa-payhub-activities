package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class SendClient {

  private final SendApisHolder sendApisHolder;

  public SendClient(SendApisHolder sendApisHolder) {
    this.sendApisHolder = sendApisHolder;
  }

  public void preloadSendFile(String accessToken, String sendNotificationId) {
    sendApisHolder.getSendApi(accessToken).preloadSendFile(sendNotificationId);
  }

  public void uploadSendFile(String accessToken, String sendNotificationId) {
    sendApisHolder.getSendApi(accessToken).uploadSendFile(sendNotificationId);
  }

  public void deliveryNotification(String accessToken, String sendNotificationId) {
    sendApisHolder.getSendApi(accessToken).deliveryNotification(sendNotificationId);
  }

  public SendNotificationDTO notificationStatus(String accessToken, String sendNotificationId) {
    return sendApisHolder.getSendApi(accessToken).notificationStatus(sendNotificationId);
  }

  public SendNotificationDTO retrieveNotificationDate(String accessToken, String sendNotificationId, Long organizationId) {
    return sendApisHolder.getSendApi(accessToken).retrieveNotificationDate(sendNotificationId, organizationId);
  }

}
