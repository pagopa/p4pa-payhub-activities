package it.gov.pagopa.payhub.activities.activity.sendnotification.create;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeConflictException;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendNotificationConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

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

        verify(sendServiceMock).deliveryNotification("sendNotificationId");
    }
    @Test
    void whenDeliveryNotificationThenThrowSendNotificationConflictException() {
        String sendNotificationId = "sendNotificationId";

        doThrow(new RestInvokeConflictException("APPNAME", HttpStatus.CONFLICT, "ERROR", "ERRORCODE", "ERRORMESSAGE", null))
                .when(sendServiceMock).deliveryNotification(sendNotificationId);

        SendNotificationConflictException exception = assertThrows(
                SendNotificationConflictException.class,
                () -> deliveryNotificationActivity.deliverySendNotification(sendNotificationId)
        );

        assertEquals(
                "Conflict error while deliverySendNotification for sendNotificationId " + sendNotificationId,
                exception.getMessage()
        );
        verify(sendServiceMock).deliveryNotification(sendNotificationId);
    }

}