package it.gov.pagopa.payhub.activities.activity.sendnotification.stream.processing;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendStreamSkippedEventException;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
@Lazy
public class GetSendNotificationByNotificationRequestIdActivityImpl implements GetSendNotificationByNotificationRequestIdActivity {

    private final SendService sendService;

    public GetSendNotificationByNotificationRequestIdActivityImpl(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public SendNotificationDTO getSendNotificationByNotificationRequestId(String notificationRequestId) {
        log.info("Starting sendNotificationStatusValidity for notificationRequestId {}", notificationRequestId);
        try {
            return sendService.retrieveNotificationByNotificationRequestId(notificationRequestId);
        } catch (HttpClientErrorException.NotFound e) {
            String errorMessage = "Notification for notificationRequestId %s not found: error message %s".formatted(notificationRequestId, e.getMessage());
            throw new SendStreamSkippedEventException("Skipped an error during execution of activity %s: %s".formatted(ValidateSendNotificationStatusActivity.class.getSimpleName(), errorMessage));
        }
    }
}
