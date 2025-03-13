package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.NewNotificationRequestStatusResponseV24DTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SendServiceTest {

    @Mock
    private SendClient sendClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private SendService sendService;

    @BeforeEach
    void setUp() {
        sendService = new SendServiceImpl(sendClientMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(sendClientMock);
    }

    @Test
    void givenSendNotificationIdWhenPreloadSendFileThenOk() {
        // Given
        String sendNotificationId = "sendNotificationId";

        // When
        sendService.preloadSendFile(sendNotificationId);

        // Then
        Mockito.verify(sendClientMock).preloadSendFile(null, sendNotificationId);
    }

    @Test
    void givenSendNotificationIdWhenUploadSendFileThenOk() {
        // Given
        String sendNotificationId = "sendNotificationId";

        // When
        sendService.uploadSendFile(sendNotificationId);

        // Then
        Mockito.verify(sendClientMock).uploadSendFile(null, sendNotificationId);
    }

    @Test
    void givenSendNotificationIdWhenDeliveryNotificationThenOk() {
        // Given
        String sendNotificationId = "sendNotificationId";

        // When
        sendService.deliveryNotification(sendNotificationId);

        // Then
        Mockito.verify(sendClientMock).deliveryNotification(null, sendNotificationId);
    }

    @Test
    void givenSendNotificationIdWhenNotificationStatusThenOk() {
        // Given
        String sendNotificationId = "sendNotificationId";
        NewNotificationRequestStatusResponseV24DTO expectedResponse = new NewNotificationRequestStatusResponseV24DTO();

        // When
        Mockito.when(sendClientMock.notificationStatus(null, sendNotificationId)).thenReturn(expectedResponse);

        NewNotificationRequestStatusResponseV24DTO result = sendService.notificationStatus(sendNotificationId);

        // Then
        assertEquals(expectedResponse, result);
    }
}
