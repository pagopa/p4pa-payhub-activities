package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class UploadSendFileActivityImpl implements UploadSendFileActivity {
    private final SendService sendService;

    public UploadSendFileActivityImpl(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public void uploadSendFile(String sendNotificationId) {
        log.info("Starting uploadSendFile for sendNotificationId {}", sendNotificationId);
        sendService.uploadSendFile(sendNotificationId);
    }
}
