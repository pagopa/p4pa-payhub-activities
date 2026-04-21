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
public class DeleteSendLegalFactFileActivityImpl implements DeleteSendLegalFactFileActivity {
    private final SendNotificationService sendNotificationService;

    public DeleteSendLegalFactFileActivityImpl(SendNotificationService sendNotificationService) {
        this.sendNotificationService = sendNotificationService;
    }

    public OffsetDateTime deleteSendLegalFactFile(String sendNotificationId) {
        log.info("Starting deleteSendLegalFactFile for sendNotificationId {}", sendNotificationId);
        FileExpirationResponseDTO response = sendNotificationService.deleteExpiredLegalFacts(sendNotificationId);
        return response!=null? response.getNextFileExpirationDate() : null;
    }
}
