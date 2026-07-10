package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.TimelineElementCategoryV27DTO;

import java.util.List;
import java.util.Map;

@ActivityInterface
public interface NotifySendNotificationTimelineCategoryActivity {
    @ActivityMethod
    void notifySendNotificationTimelineCategory(Map<String, List<TimelineElementCategoryV27DTO>> requestBody);
}
