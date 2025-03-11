package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class PreloadSendFileActivityImpl implements PreloadSendFileActivity {
    private final SendService sendService;

    public PreloadSendFileActivityImpl(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public void preloadSendFile(String sendNotificationId) {
        log.info("Starting preloadSendFile for sendNotificationId {}", sendNotificationId);
        sendService.preloadSendFile(sendNotificationId);
    }
}
