package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendNotificationConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeliveryNotificationActivityTest {

    @Mock
    private SendService sendServiceMock;

    private DeliveryNotificationActivity deliveryNotificationActivity;

    @BeforeEach
    void init() {
        deliveryNotificationActivity = new DeliveryNotificationActivityImpl(sendServiceMock);
    }

    @Test
    void whenDeliveryNotificationThenVoid() {
        deliveryNotificationActivity.deliverySendNotification("sendNotificationId");

        Mockito.verify(sendServiceMock).deliveryNotification("sendNotificationId");
    }
    @Test
    void whenDeliveryNotificationThenThrowSendNotificationConflictException() {
        String sendNotificationId = "sendNotificationId";

        Mockito.doThrow(HttpClientErrorException.Conflict.create(
                "Conflict",
                HttpStatus.CONFLICT,
                "409 Conflict",
                null,
                null,
                null
        )).when(sendServiceMock).deliveryNotification(sendNotificationId);

        SendNotificationConflictException exception = assertThrows(
                SendNotificationConflictException.class,
                () -> deliveryNotificationActivity.deliverySendNotification(sendNotificationId)
        );

        assertEquals(
                "Conflict error while deliverySendNotification for sendNotificationId " + sendNotificationId,
                exception.getMessage()
        );
        Mockito.verify(sendServiceMock).deliveryNotification(sendNotificationId);
    }

}