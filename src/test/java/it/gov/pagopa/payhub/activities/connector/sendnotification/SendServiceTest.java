package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionSearchClient;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.InstallmentClient;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class SendServiceTest {

    @Mock
    private SendClient sendClientMock;
    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private InstallmentClient installmentClient;
    @Mock
    private DebtPositionSearchClient debtPositionSearchClient;

    private SendService sendService;

    @BeforeEach
    void setUp() {
        sendService = new SendServiceImpl(sendClientMock, authnServiceMock, installmentClient, debtPositionSearchClient);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                sendClientMock, authnServiceMock);
    }

    @Test
    void givenSendNotificationIdWhenPreloadSendFileThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        sendService.preloadSendFile(sendNotificationId);

        // Then
        Mockito.verify(sendClientMock).preloadSendFile(sendNotificationId, accessToken);
    }

    @Test
    void givenSendNotificationIdWhenUploadSendFileThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        sendService.uploadSendFile(sendNotificationId);

        // Then
        Mockito.verify(sendClientMock).uploadSendFile(sendNotificationId, accessToken);
    }

    @Test
    void givenSendNotificationIdWhenDeliveryNotificationThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        sendService.deliveryNotification(sendNotificationId);

        // Then
        Mockito.verify(sendClientMock).deliveryNotification(sendNotificationId, accessToken);
    }

    @Test
    void givenSendNotificationIdWhenNotificationStatusThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        Mockito.when(sendClientMock.notificationStatus(sendNotificationId, accessToken))
                .thenReturn(expectedResponse);

        SendNotificationDTO result = sendService.notificationStatus(sendNotificationId);

        // Then
        assertSame(expectedResponse, result);
    }

    @Test
    void givenSendNotificationIdAndOrganizationIdWhenRetrieveNotificationDateThenOk() {
        // Given
        String sendNotificationId = "sendNotificationId";
        Long organizationId = 1L;

        // When
        Mockito.when(sendClientMock.retrieveNotificationDate(null, sendNotificationId)).thenReturn(null);

        SendNotificationDTO result = sendService.retrieveNotificationDate(null, sendNotificationId, organizationId);

        // Then
        assertNull(result);
    }
}
