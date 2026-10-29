package it.gov.pagopa.payhub.activities.activity.sendnotification.stream.processing;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationRefusedErrorV27DTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateSendNotificationRefusalReasonsActivityImplTest {

    @Mock
    private SendNotificationService sendNotificationServiceMock;

    @InjectMocks
    private UpdateNotificationRefusalReasonsActivityImpl activity;

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
        List<NotificationRefusedErrorV27DTO> refusalReasons = List.of(new NotificationRefusedErrorV27DTO());

        //WHEN
        activity.updateNotificationRefusalReasons(notificationRequestId, refusalReasons);

        //THEN
        verify(sendNotificationServiceMock)
                .updateNotificationRefusalReasons(
                        notificationRequestId,
                        refusalReasons
                );
    }

    @Test
    void givenEmptyRefusalReasonsWhenUpdateSendNotificationStatus() {
        //GIVEN
        String notificationRequestId = "requestId";
        List<NotificationRefusedErrorV27DTO> refusalReasons = Collections.emptyList();

        //WHEN
        activity.updateNotificationRefusalReasons(notificationRequestId, refusalReasons);

        //THEN
        verify(sendNotificationServiceMock, never())
                .updateNotificationRefusalReasons(
                        anyString(),
                        anyList()
                );
    }

    @Test
    void givenNullRefusalReasonsWhenUpdateSendNotificationStatus() {
        //GIVEN
        String notificationRequestId = "requestId";
        List<NotificationRefusedErrorV27DTO> refusalReasons = null;

        //WHEN
        activity.updateNotificationRefusalReasons(notificationRequestId, refusalReasons);

        //THEN
        verify(sendNotificationServiceMock, never())
                .updateNotificationRefusalReasons(
                        anyString(),
                        anyList()
                );
    }
}