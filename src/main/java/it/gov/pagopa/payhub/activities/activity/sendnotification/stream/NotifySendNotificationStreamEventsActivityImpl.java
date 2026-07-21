package it.gov.pagopa.payhub.activities.activity.sendnotification.stream;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.StreamEventSummaryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Lazy
public class NotifySendNotificationStreamEventsActivityImpl implements NotifySendNotificationStreamEventsActivity {

    private final SendService sendService;

    public NotifySendNotificationStreamEventsActivityImpl(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public void notifySendNotificationStreamEvents(Map<String, List<StreamEventSummaryDTO>> notificationRequestIdToStreamEventsMap) {
        log.info("Notify stream events for send notifications with following notificationRequestId: {}", notificationRequestIdToStreamEventsMap.keySet());
        sendService.notifySendNotificationStreamEvents(notificationRequestIdToStreamEventsMap);
    }

}