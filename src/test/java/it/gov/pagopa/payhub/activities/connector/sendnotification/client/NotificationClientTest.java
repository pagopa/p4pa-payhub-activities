package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.controller.generated.NotificationApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationClientTest {

    @Mock
    private SendApisHolder sendApisHolderMock;
    @Mock
    private NotificationApi notificationApiMock;

    private NotificationClient notificationClient;

    @BeforeEach
    void setUp() {
        notificationClient = new NotificationClient(sendApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(sendApisHolderMock);
    }

    @Test
    void whenRetrieveNotificationDateThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";
        Long organizationId = 3L;

        Mockito.when(sendApisHolderMock.getNotificationApi(accessToken))
                .thenReturn(notificationApiMock);

        // When
        notificationClient.retrieveNotificationDate(accessToken, sendNotificationId, organizationId);

        // Then
        Mockito.verify(notificationApiMock).retrieveNotificationDate(sendNotificationId, organizationId);
    }

}
