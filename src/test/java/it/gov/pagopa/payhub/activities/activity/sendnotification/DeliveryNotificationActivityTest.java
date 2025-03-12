package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void whenPreloadSendFileThenVoid() {
        deliveryNotificationActivity.deliveryNotification("sendNotificationId");

        Mockito.verify(sendServiceMock).deliveryNotification("sendNotificationId");
    }

}