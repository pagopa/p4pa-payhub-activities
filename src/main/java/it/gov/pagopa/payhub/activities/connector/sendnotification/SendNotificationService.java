package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.pu.sendnotification.dto.generated.*;

import java.util.List;

public interface SendNotificationService {
  SendNotificationDTO getSendNotification(String sendNotificationId);
  CreateNotificationResponse createSendNotification(CreateNotificationRequest createNotificationRequest);
  SendNotificationDTO findSendNotificationByOrgIdAndNav(Long organizationId, String nav);
  StartNotificationResponse startSendNotification(String sendNotificationId, LoadFileRequest loadFileRequest);
  SendStreamDTO findSendStream(String sendStreamId);
  List<ProgressResponseElementV25DTO> readSendStreamEvents(Long organizationId, String sendStreamId, String lastEventId);
}
