package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactDownloadMetadataDTO;
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
        Mockito.when(sendClientMock.notificationStatus(sendNotificationId, accessToken))
                .thenReturn(expectedResponse);

        // When
        SendNotificationDTO result = sendService.notificationStatus(sendNotificationId);

        // Then
        assertSame(expectedResponse, result);
    }

    @Test
    void givenSendNotificationIdWhenRetrieveNotificationDateThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.when(sendClientMock.retrieveNotificationDate(sendNotificationId, accessToken))
                .thenReturn(expectedResponse);

        // When
        SendNotificationDTO result = sendService.retrieveNotificationDate(sendNotificationId);

        // Then
        assertSame(expectedResponse, result);
    }

    @Test
    void givenSendNotificationIdWhenRetrieveNotificationByNotificationRequestIdThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String notificationRequestId = "notificationRequestId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.when(sendClientMock.retrieveNotificationByNotificationRequestId(notificationRequestId, accessToken))
                .thenReturn(expectedResponse);

        // When
        SendNotificationDTO result = sendService.retrieveNotificationByNotificationRequestId(notificationRequestId);

        // Then
        assertSame(expectedResponse, result);
    }

    @Test
    void givenValidRequestWhenRetrieveLegalFactDownloadMetadataThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";
        String legalFactId = "sendLegalFact.pdf";

        LegalFactDownloadMetadataDTO expectedResponse = new LegalFactDownloadMetadataDTO();

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.when(sendClientMock.retrieveLegalFactDownloadMetadata(sendNotificationId, legalFactId, accessToken))
                .thenReturn(expectedResponse);

        // When
        LegalFactDownloadMetadataDTO actualResult = sendService.retrieveLegalFactDownloadMetadata(
                sendNotificationId,
                legalFactId
        );

        // Then
        assertSame(expectedResponse, actualResult);
    }
}
