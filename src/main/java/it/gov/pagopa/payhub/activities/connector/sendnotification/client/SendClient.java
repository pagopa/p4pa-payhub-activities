package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

}
