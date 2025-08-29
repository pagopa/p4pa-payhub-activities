package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationResponse;
import it.gov.pagopa.pu.sendnotification.dto.generated.LoadFileRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.StartNotificationResponse;

public interface SendNotificationService {
  SendNotificationDTO getSendNotification(String sendNotificationId);
  CreateNotificationResponse createSendNotification(CreateNotificationRequest createNotificationRequest);
  SendNotificationDTO findSendNotificationByOrgIdAndNav(Long organizationId, String nav);
  StartNotificationResponse startSendNotification(String sendNotificationId, LoadFileRequest loadFileRequest);
  void updateNotificationStatus(String sendNotificationId, String status);
}
