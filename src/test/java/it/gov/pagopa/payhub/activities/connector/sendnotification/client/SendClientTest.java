package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.controller.generated.SendApi;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class SendClientTest {

    @Mock
    private SendApisHolder sendApisHolderMock;
    @Mock
    private SendApi sendApiMock;

    private SendClient sendClient;

    @BeforeEach
    void setUp() {
        sendClient = new SendClient(sendApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(sendApisHolderMock);
    }

    @Test
    void whenPreloadSendFileThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";

        Mockito.when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);

        // When
        sendClient.preloadSendFile(sendNotificationId, accessToken);

        // Then
        Mockito.verify(sendApiMock).preloadSendFile(sendNotificationId);
    }

    @Test
    void whenUploadSendFileThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";

        Mockito.when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);

        // When
        sendClient.uploadSendFile(sendNotificationId, accessToken);

        // Then
        Mockito.verify(sendApiMock).uploadSendFile(sendNotificationId);
    }

    @Test
    void whenDeliveryNotificationThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";

        Mockito.when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);

        // When
        sendClient.deliveryNotification(sendNotificationId, accessToken);

        // Then
        Mockito.verify(sendApiMock).deliveryNotification(sendNotificationId);
    }

    @Test
    void whenNotificationStatusThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        Mockito.when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);
        Mockito.when(sendApiMock.notificationStatus(sendNotificationId))
                .thenReturn(expectedResponse);

        // When
        SendNotificationDTO result = sendClient.notificationStatus(sendNotificationId, accessToken);

        // Then
        assertSame(expectedResponse, result);
    }

}
