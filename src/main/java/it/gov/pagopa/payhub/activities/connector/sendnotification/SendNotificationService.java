package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

public interface SendNotificationService {
  SendNotificationDTO getSendNotification(String sendNotificationId);
}
