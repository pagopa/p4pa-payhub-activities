package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.NotificationClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationClient notificationClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(notificationClientMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(notificationClientMock);
    }

    @Test
    void givenSendNotificationIdWhenRetrieveNotificationDateThenOk() {
        // Given
        String sendNotificationId = "sendNotificationId";
        Long organizationId = 3L;

        // When
        notificationService.retrieveNotificationDate(sendNotificationId, organizationId);

        // Then
        Mockito.verify(notificationClientMock).retrieveNotificationDate(null, sendNotificationId, organizationId);
    }
}
