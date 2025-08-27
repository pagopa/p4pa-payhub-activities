package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendNotificationClient;
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

}
