package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendNotificationClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
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
    void givenSendNotificationIdWhenPreloadSendFileThenOk() {
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

}
