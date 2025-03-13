package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.NewNotificationRequestStatusResponseV24DTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        NewNotificationRequestStatusResponseV24DTO expectedResponse = new NewNotificationRequestStatusResponseV24DTO();

        // When
        Mockito.when(sendServiceMock.notificationStatus(notificationId)).thenReturn(expectedResponse);

        NewNotificationRequestStatusResponseV24DTO result = notificationStatusActivity.notificationStatus("sendNotificationId");

        // Then
        assertEquals(expectedResponse, result);
    }

}