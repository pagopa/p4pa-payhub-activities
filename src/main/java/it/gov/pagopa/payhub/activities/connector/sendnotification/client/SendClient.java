package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
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

}
