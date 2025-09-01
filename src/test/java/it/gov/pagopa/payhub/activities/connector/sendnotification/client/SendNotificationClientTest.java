package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.controller.generated.NotificationApi;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationResponse;
import it.gov.pagopa.pu.sendnotification.dto.generated.LoadFileRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.StartNotificationResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class SendNotificationClientTest {

    @Mock
    private SendApisHolder sendApisHolderMock;
    @Mock
    private NotificationApi sendNotificationApiMock;

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

        Mockito.when(sendApisHolderMock.getSendNotificationApi(accessToken))
                .thenReturn(sendNotificationApiMock);
        Mockito.when(sendNotificationApiMock.getSendNotification(Mockito.same(sendNotificationId)))
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

        Mockito.when(sendApisHolderMock.getSendNotificationApi(accessToken))
                .thenReturn(sendNotificationApiMock);
        Mockito.when(sendNotificationApiMock.getSendNotification(Mockito.same(sendNotificationId)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

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

        Mockito.when(sendApisHolderMock.getSendNotificationApi(accessToken))
            .thenReturn(sendNotificationApiMock);
        Mockito.when(sendNotificationApiMock.createSendNotification(request))
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

        Mockito.when(sendApisHolderMock.getSendNotificationApi(accessToken))
            .thenReturn(sendNotificationApiMock);
        Mockito.when(sendNotificationApiMock.createSendNotification(request))
            .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

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

        Mockito.when(sendApisHolderMock.getSendNotificationApi(accessToken))
            .thenReturn(sendNotificationApiMock);
        Mockito.when(sendNotificationApiMock.findSendNotificationByOrgIdAndNav(organizationId, nav))
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

        Mockito.when(sendApisHolderMock.getSendNotificationApi(accessToken))
            .thenReturn(sendNotificationApiMock);
        Mockito.when(sendNotificationApiMock.findSendNotificationByOrgIdAndNav(organizationId, nav))
            .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

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

        Mockito.when(sendApisHolderMock.getSendNotificationApi(accessToken))
            .thenReturn(sendNotificationApiMock);
        Mockito.when(sendNotificationApiMock.startNotification(sendNotificationId, new LoadFileRequest()))
            .thenReturn(expectedResponse);

        // When
        StartNotificationResponse result = client.startSendNotification(sendNotificationId, new LoadFileRequest(), accessToken);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse, result);
    }

}
