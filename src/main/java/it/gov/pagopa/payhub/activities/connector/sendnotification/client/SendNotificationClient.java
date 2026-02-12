package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.util.List;

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

  public SendNotificationDTO findSendNotificationByOrgIdAndNav(Long organizationId, String nav, String accessToken) {
    try {
      return sendApisHolder.getSendNotificationApi(accessToken)
          .findSendNotificationByOrgIdAndNav(organizationId, nav);
    } catch (HttpClientErrorException.NotFound e){
      log.info("Cannot find SendNotification having orgId {} and nav {}", organizationId, nav);
      return null;
    }
  }

  public CreateNotificationResponse createSendNotification(CreateNotificationRequest createNotificationRequest, String accessToken){
    try {
      return sendApisHolder.getSendNotificationApi(accessToken)
          .createSendNotification(createNotificationRequest);
    } catch (HttpClientErrorException exception){
      log.info("Cannot create SendNotification");
      return null;
    }
  }

  public StartNotificationResponse startSendNotification(String sendNotificationId, LoadFileRequest loadFileRequest, String accessToken) {
      return sendApisHolder.getSendNotificationApi(accessToken)
          .startNotification(sendNotificationId, loadFileRequest);
  }

  public SendStreamDTO findSendStream(String sendStreamId, String accessToken) {
    return sendApisHolder.getSendStreamsApi(accessToken)
            .getStream(sendStreamId);
  }

  public List<ProgressResponseElementV25DTO> readSendStreamEvents(Long organizationId, String sendStreamId, String lastEventId, String accessToken) {
    return sendApisHolder.getSendStreamsApi(accessToken)
            .getStreamEvents(organizationId, sendStreamId, lastEventId);
  }

  public void uploadSendLegalFact(String sendNotificationId, LegalFactCategoryDTO category, String fileName, File legalFactFile, String accessToken) {
    sendApisHolder.getSendNotificationApi(accessToken)
            .uploadSendLegalFact(sendNotificationId, category, fileName, legalFactFile);
  }
}
