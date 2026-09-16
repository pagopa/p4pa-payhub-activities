package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.sendnotification.client.generated.NotificationApi;
import it.gov.pagopa.pu.sendnotification.client.generated.StreamsApi;
import it.gov.pagopa.pu.sendnotification.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendNotificationClientTest {

    @Mock
    private SendApisHolder sendApisHolderMock;
    @Mock
    private NotificationApi sendNotificationApiMock;
    @Mock
    private StreamsApi sendStreamsApiMock;

    private SendNotificationClient client;

    @BeforeEach
    void setUp() {
        client = new SendNotificationClient(sendApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(sendApisHolderMock);
    }

    @Test
    void whenFindSendNotificationThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";

        SendNotificationDTO expectedResult = new SendNotificationDTO();

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
                .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.getSendNotification(Mockito.same(sendNotificationId)))
                .thenReturn(expectedResult);

        // When
        SendNotificationDTO result = client.findSendNotification(sendNotificationId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentSendNotificationWhenFindSendNotificationThenReturnNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "notificationId";

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
                .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.getSendNotification(Mockito.same(sendNotificationId)))
                .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        // When
        SendNotificationDTO result = client.findSendNotification(sendNotificationId, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenCreateSendNotificationThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";

        CreateNotificationRequest request = new CreateNotificationRequest();
        CreateNotificationResponse expectedResult = new CreateNotificationResponse();

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
            .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.createSendNotification(request))
            .thenReturn(expectedResult);

        // When
        CreateNotificationResponse result = client.createSendNotification(request, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentSendNotificationWhenCreateSendNotificationThenReturnNull() {
        // Given
        String accessToken = "ACCESSTOKEN";

        CreateNotificationRequest request = new CreateNotificationRequest();

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
            .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.createSendNotification(request))
            .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        // When
        CreateNotificationResponse result = client.createSendNotification(request, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenFindSendNotificationByOrgIdAndNavThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String nav = "NAV";
        Long organizationId = 1L;
        SendNotificationDTO expectedResult = new SendNotificationDTO();

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
            .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.findSendNotificationByOrgIdAndNav(organizationId, nav))
            .thenReturn(expectedResult);

        // When
        SendNotificationDTO result = client.findSendNotificationByOrgIdAndNav(organizationId, nav, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentSendNotificationWhenFindSendNotificationByOrgIdAndNavThenReturnNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String nav = "NAV";
        Long organizationId = 1L;

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
            .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.findSendNotificationByOrgIdAndNav(organizationId, nav))
            .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        // When
        SendNotificationDTO result = client.findSendNotificationByOrgIdAndNav(organizationId, nav, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void givenValidRequestWhenStartSendNotificationThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "NOTIFICATIONID";

        StartNotificationResponse expectedResponse = new StartNotificationResponse();

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
            .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.startNotification(sendNotificationId, new LoadFileRequest()))
            .thenReturn(expectedResponse);

        // When
        StartNotificationResponse result = client.startSendNotification(sendNotificationId, new LoadFileRequest(), accessToken);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse, result);
    }

    @Test
    void givenValidRequestWhenFindSendStreamThenOk() {
        //Given
        String accessToken = "ACCESSTOKEN";
        String sendStreamId = "sendStreamId";

        SendStreamDTO expectedResult = new SendStreamDTO();

        when(sendApisHolderMock.getSendStreamsApi(accessToken))
                .thenReturn(sendStreamsApiMock);
        when(sendStreamsApiMock.getStream(sendStreamId))
                .thenReturn(expectedResult);

        //When
        SendStreamDTO actualResult = client.findSendStream(sendStreamId, accessToken);

        //Then
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void givenValidRequestWhenReadSendStreamEventsThenOk() {
        //Given
        String accessToken = "ACCESSTOKEN";
        Long organizationId = 1L;
        String streamId = "streamId";

        List<ProgressResponseElementV28DTO> expectedResult = List.of(new ProgressResponseElementV28DTO());

        when(sendApisHolderMock.getSendStreamsApi(accessToken))
                .thenReturn(sendStreamsApiMock);
        when(sendStreamsApiMock.getStreamEvents(organizationId, streamId))
                .thenReturn(expectedResult);

        //When
        List<ProgressResponseElementV28DTO> actualResult = client.readSendStreamEvents(organizationId, streamId, accessToken);

        //Then
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void givenValidRequestWhenUpdateLastProcessedStreamEventIdThenOk() {
        //Given
        String accessToken = "ACCESSTOKEN";
        String streamId = "streamId";
        String lastEventId = "lastEventId";

        when(sendApisHolderMock.getSendStreamsApi(accessToken))
                .thenReturn(sendStreamsApiMock);
        Mockito.doNothing()
                .when(sendStreamsApiMock)
                .updateStreamLastEventId(streamId, lastEventId);

        //When
        client.updateLastProcessedStreamEventId(streamId, lastEventId, accessToken);

        //Then
        verify(sendStreamsApiMock)
                .updateStreamLastEventId(
                    streamId,
                    lastEventId
                );
    }

    @Test
    void givenValidRequestWhenUpdateSendNotificationStatusThenOk() {
        //Given
        String accessToken = "ACCESSTOKEN";
        String notificationRequestId = "requestId";

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
                .thenReturn(sendNotificationApiMock);
        Mockito.doNothing()
                .when(sendNotificationApiMock)
                .updateNotificationStatus(notificationRequestId, NotificationStatus.DELIVERED);

        //When
        client.updateSendNotificationStatus(notificationRequestId, NotificationStatus.DELIVERED, accessToken);

        //Then
        verify(sendNotificationApiMock)
                .updateNotificationStatus(
                        notificationRequestId,
                        NotificationStatus.DELIVERED
                );
    }

    @Test
    void whenDeleteExpiredLegalFactsThenInvokeWithAccessToken() {
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";
        FileExpirationResponseDTO expectedResult = new FileExpirationResponseDTO();

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
                .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.deleteExpiredLegalFacts(sendNotificationId))
                .thenReturn(expectedResult);

        FileExpirationResponseDTO result = client.deleteExpiredLegalFacts(sendNotificationId, accessToken);

        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotFoundWhenDeleteExpiredLegalFactsThenReturnNull() {
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
                .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.deleteExpiredLegalFacts(sendNotificationId))
                .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        FileExpirationResponseDTO result = client.deleteExpiredLegalFacts(sendNotificationId, accessToken);

        Assertions.assertNull(result);
    }

    @Test
    void whenDeleteExpiredDocumentsThenInvokeWithAccessToken() {
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";
        FileExpirationResponseDTO expectedResult = new FileExpirationResponseDTO();

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
                .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.deleteExpiredDocuments(sendNotificationId))
                .thenReturn(expectedResult);

        FileExpirationResponseDTO result = client.deleteExpiredDocuments(sendNotificationId, accessToken);

        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotFoundWhenDeleteExpiredDocumentsThenReturnNull() {
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";

        when(sendApisHolderMock.getSendNotificationApi(accessToken))
                .thenReturn(sendNotificationApiMock);
        when(sendNotificationApiMock.deleteExpiredDocuments(sendNotificationId))
                .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        FileExpirationResponseDTO result = client.deleteExpiredDocuments(sendNotificationId, accessToken);

        Assertions.assertNull(result);
    }
}
