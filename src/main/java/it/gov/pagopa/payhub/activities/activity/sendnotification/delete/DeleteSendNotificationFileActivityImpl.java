package it.gov.pagopa.payhub.activities.activity.sendnotification.delete;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.FileExpirationResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@Lazy
public class DeleteSendNotificationFileActivityImpl implements DeleteSendNotificationFileActivity {
    private final SendNotificationService sendNotificationService;

    public DeleteSendNotificationFileActivityImpl(SendNotificationService sendNotificationService) {
        this.sendNotificationService = sendNotificationService;
    }

    public OffsetDateTime deleteSendNotificationExpiredFiles(String sendNotificationId) {
        log.info("Starting deleteSendNotificationExpiredFiles for sendNotificationId {}", sendNotificationId);
        FileExpirationResponseDTO response = sendNotificationService.deleteExpiredDocuments(sendNotificationId);
        return response!=null? response.getNextFileExpirationDate() : null;
    }
}
