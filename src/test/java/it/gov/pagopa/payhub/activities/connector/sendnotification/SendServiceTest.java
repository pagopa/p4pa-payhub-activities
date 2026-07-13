package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.TimelineElementCategoryV27DTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        sendService.preloadSendFile(sendNotificationId);

        // Then
        verify(sendClientMock).preloadSendFile(sendNotificationId, accessToken);
    }

    @Test
    void givenSendNotificationIdWhenUploadSendFileThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        sendService.uploadSendFile(sendNotificationId);

        // Then
        verify(sendClientMock).uploadSendFile(sendNotificationId, accessToken);
    }

    @Test
    void givenSendNotificationIdWhenDeliveryNotificationThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        sendService.deliveryNotification(sendNotificationId);

        // Then
        verify(sendClientMock).deliveryNotification(sendNotificationId, accessToken);
    }

    @Test
    void givenSendNotificationIdWhenNotificationStatusThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        when(sendClientMock.notificationStatus(sendNotificationId, accessToken))
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

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        when(sendClientMock.retrieveNotificationDate(sendNotificationId, accessToken))
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

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        when(sendClientMock.retrieveNotificationByNotificationRequestId(notificationRequestId, accessToken))
                .thenReturn(expectedResponse);

        // When
        SendNotificationDTO result = sendService.retrieveNotificationByNotificationRequestId(notificationRequestId);

        // Then
        assertSame(expectedResponse, result);
    }

    @Test
    void givenValidRequestWhenUploadSendLegalFactThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String notificationRequestId = "notificationRequestId";
        LegalFactCategoryDTO category = LegalFactCategoryDTO.ANALOG_DELIVERY;
        String legalFactId = "legalFactFile.pdf";

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.doNothing()
                .when(sendClientMock)
                .downloadAndArchiveSendLegalFact(
                    notificationRequestId,
                    category,
                    legalFactId,
                    accessToken
                );

        // When
        sendService.downloadAndArchiveSendLegalFact(
                notificationRequestId,
                category,
                legalFactId
        );

        // Then
        verify(sendClientMock)
                .downloadAndArchiveSendLegalFact(
                    notificationRequestId,
                    category,
                    legalFactId,
                    accessToken
                );
    }

    @Test
    void notifySendNotificationTimelineCategory() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Map<String, List<TimelineElementCategoryV27DTO>> map = new HashMap<>();

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.doNothing()
                .when(sendClientMock)
                .notifySendNotificationTimelineCategory(
                    map,
                    accessToken
                );

        // When
        sendService.notifySendNotificationTimelineCategory(
              map
        );

        // Then
        verify(sendClientMock)
                .notifySendNotificationTimelineCategory(
                        map,
                        accessToken
                );
    }

}
