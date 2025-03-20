package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class NotificationClient {

  private final SendApisHolder sendApisHolder;

  public NotificationClient(SendApisHolder sendApisHolder) {
    this.sendApisHolder = sendApisHolder;
  }

  public SendNotificationDTO retrieveNotificationDate(String accessToken, String sendNotificationId, Long organizationId) {
    return sendApisHolder.getNotificationApi(accessToken).retrieveNotificationDate(sendNotificationId, organizationId);
  }

}
