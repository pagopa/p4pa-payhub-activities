package it.gov.pagopa.payhub.activities.activity.sendnotification.stream;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.StreamEventSummaryDTO;

import java.util.List;
import java.util.Map;

@ActivityInterface
public interface NotifySendNotificationStreamEventsActivity {
    @ActivityMethod
    void notifySendNotificationStreamEvents(Map<String, List<StreamEventSummaryDTO>> notificationRequestIdToStreamEventsMap);
}
