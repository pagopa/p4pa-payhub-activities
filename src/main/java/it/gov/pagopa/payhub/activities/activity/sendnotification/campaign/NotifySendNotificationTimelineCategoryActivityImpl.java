package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.TimelineElementCategoryV27DTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Lazy
public class NotifySendNotificationTimelineCategoryActivityImpl implements NotifySendNotificationTimelineCategoryActivity {

    private final SendService sendService;

    public NotifySendNotificationTimelineCategoryActivityImpl(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public void notifySendNotificationTimelineCategory(Map<String, List<TimelineElementCategoryV27DTO>> notificationRequestIdToTimelineCatogoriesMap) {
        log.info("Notify TimelineCategories for send notifications with following notificationRequestId: {}", notificationRequestIdToTimelineCatogoriesMap.keySet());
        sendService.notifySendNotificationTimelineCategory(notificationRequestIdToTimelineCatogoriesMap);
    }

}