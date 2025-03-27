package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Lazy
@Service
@Slf4j
public class SendNotificationClient {

  private final SendApisHolder sendApisHolder;

  public SendNotificationClient(SendApisHolder sendApisHolder) {
    this.sendApisHolder = sendApisHolder;
  }

  public SendNotificationDTO findSendNotification(String sendNotificationId, String accessToken) {
    try {
      return sendApisHolder.getSendNotificationApi(accessToken)
              .getSendNotification(sendNotificationId);
    } catch (HttpClientErrorException.NotFound e){
      log.info("Cannot find SendNotification having id {}", sendNotificationId);
      return null;
    }
  }


}
