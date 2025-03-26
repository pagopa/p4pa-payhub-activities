package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class NotificationStatusActivityTest {

    @Mock
    private SendService sendServiceMock;

    private NotificationStatusActivity notificationStatusActivity;

    @BeforeEach
    void init() {
        notificationStatusActivity = new NotificationStatusActivityImpl(sendServiceMock);
    }


    @Test
    void whenPreloadSendFileThenVoid() {
        // Given
        String notificationId = "sendNotificationId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        // When
        Mockito.when(sendServiceMock.notificationStatus(notificationId)).thenReturn(expectedResponse);

        SendNotificationDTO result = notificationStatusActivity.getSendNotificationStatus("sendNotificationId");

        // Then
        assertSame(expectedResponse, result);
    }

}