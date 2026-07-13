package it.gov.pagopa.payhub.activities.activity.sendnotification.stream;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.TimelineElementCategoryV27DTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotifySendNotificationTimelineCategoryActivityTest {

    @Mock
    private SendService sendServiceMock;

    @InjectMocks
    private NotifySendNotificationTimelineCategoryActivityImpl activity;

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
            sendServiceMock
        );
    }

    @Test
    void notifySendNotificationTimelineCategory() {
        //GIVEN
        Map<String, List<TimelineElementCategoryV27DTO>> map = new HashMap<>();
        //WHEN
        activity.notifySendNotificationTimelineCategory(map);
        //THEN
        verify(sendServiceMock).notifySendNotificationTimelineCategory(map);
    }
}