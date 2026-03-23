package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateSendNotificationStatusActivityImplTest {

    @Mock
    private SendNotificationService sendNotificationServiceMock;

    @InjectMocks
    private UpdateSendNotificationStatusActivityImpl activity;

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                sendNotificationServiceMock
        );
    }

    @Test
    void updateSendNotificationStatus() {
        //GIVEN
        String notificationRequestId = "requestId";

        Mockito.doNothing()
                .when(sendNotificationServiceMock)
                .updateSendNotificationStatus(
                        notificationRequestId,
                        NotificationStatus.REGISTERED
                );

        //WHEN
        activity.UpdateSendNotificationStatus(notificationRequestId, NotificationStatus.REGISTERED);

        //THEN
        Mockito.verify(sendNotificationServiceMock)
                .updateSendNotificationStatus(
                        notificationRequestId,
                        NotificationStatus.REGISTERED
                );
    }
}