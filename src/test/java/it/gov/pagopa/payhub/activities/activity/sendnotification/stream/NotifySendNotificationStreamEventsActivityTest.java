package it.gov.pagopa.payhub.activities.activity.sendnotification.stream;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.StreamEventSummaryDTO;
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
class NotifySendNotificationStreamEventsActivityTest {

    @Mock
    private SendService sendServiceMock;

    @InjectMocks
    private NotifySendNotificationStreamEventsActivityImpl activity;

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
            sendServiceMock
        );
    }

    @Test
    void givenValidRequestWhenNotifySendNotificationStreamEventsThenOk() {
        //GIVEN
        Map<String, List<StreamEventSummaryDTO>> map = new HashMap<>();
        //WHEN
        activity.notifySendNotificationStreamEvents(map);
        //THEN
        verify(sendServiceMock).notifySendNotificationStreamEvents(map);
    }
}