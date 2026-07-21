package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.controller.generated.NotificationApi;
import it.gov.pagopa.pu.sendnotification.controller.generated.SendApi;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.StreamEventSummaryDTO;
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
class SendClientTest {

    @Mock
    private SendApisHolder sendApisHolderMock;
    @Mock
    private SendApi sendApiMock;
    @Mock
    private NotificationApi notificationApi;

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

        when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);

        // When
        sendClient.preloadSendFile(sendNotificationId, accessToken);

        // Then
        verify(sendApiMock).preloadSendFile(sendNotificationId);
    }

    @Test
    void whenUploadSendFileThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";

        when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);

        // When
        sendClient.uploadSendFile(sendNotificationId, accessToken);

        // Then
        verify(sendApiMock).uploadSendFile(sendNotificationId);
    }

    @Test
    void whenDeliveryNotificationThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";

        when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);

        // When
        sendClient.deliveryNotification(sendNotificationId, accessToken);

        // Then
        verify(sendApiMock).deliveryNotification(sendNotificationId);
    }

    @Test
    void whenNotificationStatusThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);
        when(sendApiMock.notificationStatus(sendNotificationId))
                .thenReturn(expectedResponse);

        // When
        SendNotificationDTO result = sendClient.notificationStatus(sendNotificationId, accessToken);

        // Then
        assertSame(expectedResponse, result);
    }

    @Test
    void whenRetrieveNotificationDateThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";

        when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);

        // When
        sendClient.retrieveNotificationDate(sendNotificationId, accessToken);

        // Then
        verify(sendApiMock).retrieveNotificationDate(sendNotificationId);
    }

    @Test
    void whenRetrieveNotificationByNotificationRequestIdThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String notificationRequestId = "notificationRequestId";

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
                .thenReturn(notificationApi);

        // When
        sendClient.retrieveNotificationByNotificationRequestId(notificationRequestId, accessToken);

        // Then
        verify(notificationApi).getSendNotificationByNotificationRequestId(notificationRequestId);
    }

    @Test
    void givenValidRequestWhenDownloadAndArchiveSendLegalFactThenOk() {
        //Given
        String accessToken = "ACCESSTOKEN";
        String notificationRequestId = "notificationRequestId";
        LegalFactCategoryDTO legalFactCategory = LegalFactCategoryDTO.ANALOG_DELIVERY;
        String legalFactId = "fileName.pdf";

        when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);
        Mockito.doNothing()
                .when(sendApiMock)
                .downloadAndArchiveSendLegalFact(
                    notificationRequestId,
                    legalFactCategory,
                    legalFactId
                );

        //When
        sendClient.downloadAndArchiveSendLegalFact(
                notificationRequestId,
                legalFactCategory,
                legalFactId,
                accessToken
        );

        //Then
        verify(sendApiMock)
                .downloadAndArchiveSendLegalFact(
                    notificationRequestId,
                        legalFactCategory,
                    legalFactId
                );
    }

    @Test
    void givenValidRequestWhenNotifySendNotificationStreamEventsThenOk() {
        //Given
        String accessToken = "ACCESSTOKEN";
        Map<String, List<StreamEventSummaryDTO>> map = new HashMap<>();

        when(sendApisHolderMock.getSendApi(accessToken))
                .thenReturn(sendApiMock);
        Mockito.doNothing()
                .when(sendApiMock)
                .notifySendNotificationStreamEvents(map);

        //When
        sendClient.notifySendNotificationStreamEvents(
                map,
                accessToken
        );

        //Then
        verify(sendApiMock)
                .notifySendNotificationStreamEvents(map);
    }
}
