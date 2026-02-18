package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendNotificationClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SendNotificationServiceTest {

    @Mock
    private SendNotificationClient clientMock;
    @Mock
    private AuthnService authnServiceMock;

    private SendNotificationService service;

    @BeforeEach
    void setUp() {
        service = new SendNotificationServiceImpl(clientMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(authnServiceMock, clientMock);
    }

    @Test
    void givenSendNotificationIdWhenGetSendNotificationThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO expectedResult = new SendNotificationDTO();

        Mockito.when(authnServiceMock.getAccessToken())
                        .thenReturn(accessToken);
        Mockito.when(clientMock.findSendNotification(sendNotificationId, accessToken))
                .thenReturn(expectedResult);

        // When
        SendNotificationDTO result = service.getSendNotification(sendNotificationId);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenCreateNotificationRequestWhenCreateSendNotificationThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";

        CreateNotificationRequest request = new CreateNotificationRequest();
        CreateNotificationResponse expectedResult = new CreateNotificationResponse();

        Mockito.when(authnServiceMock.getAccessToken())
            .thenReturn(accessToken);
        Mockito.when(clientMock.createSendNotification(request, accessToken))
            .thenReturn(expectedResult);

        // When
        CreateNotificationResponse result = service.createSendNotification(request);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenExistentNotificationRequestWhenFindSendNotificationByOrgIdAndNavThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long organizationId = 1L;
        String nav = "NAV";
        SendNotificationDTO expectedResult = new SendNotificationDTO();

        Mockito.when(authnServiceMock.getAccessToken())
            .thenReturn(accessToken);
        Mockito.when(clientMock.findSendNotificationByOrgIdAndNav(organizationId, nav, accessToken))
            .thenReturn(expectedResult);

        // When
        SendNotificationDTO result = service.findSendNotificationByOrgIdAndNav(organizationId, nav);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenValidRequestRequestWhenStartSendNotificationThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendNotificationId = "ID";

        StartNotificationResponse expectedResult = new StartNotificationResponse();

        Mockito.when(authnServiceMock.getAccessToken())
            .thenReturn(accessToken);
        Mockito.when(clientMock.startSendNotification(sendNotificationId, new LoadFileRequest(), accessToken))
            .thenReturn(expectedResult);

        // When
        StartNotificationResponse result = service.startSendNotification(sendNotificationId, new LoadFileRequest());

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenValidRequestWhenFindSendStreamThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String sendStreamId = "sendStreamId";

        SendStreamDTO expectedResult = new SendStreamDTO();

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.when(clientMock.findSendStream(sendStreamId, accessToken))
                .thenReturn(expectedResult);

        // When
        SendStreamDTO result = service.findSendStream(sendStreamId);

        // Then
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void givenValidRequestWhenReadSendStreamEventsThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long organizationId = 1L;
        String streamId = "streamId";

        List<ProgressResponseElementV25DTO> expectedResult = List.of(new ProgressResponseElementV25DTO());

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.when(clientMock.readSendStreamEvents(organizationId, streamId, accessToken))
                .thenReturn(expectedResult);

        // When
        List<ProgressResponseElementV25DTO> result = service.readSendStreamEvents(organizationId, streamId);

        // Then
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void givenValidRequestWhenUpdateLastProcessedStreamEventIdThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String streamId = "streamId";
        String lastEventId = "lastEventId";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.doNothing()
                .when(clientMock).updateLastProcessedStreamEventId(streamId, lastEventId, accessToken);

        // When
        service.updateLastProcessedStreamEventId(streamId, lastEventId);

        // Then
        Mockito.verify(clientMock)
                .updateLastProcessedStreamEventId(
                    streamId,
                    lastEventId,
                    accessToken
                );
    }

}
