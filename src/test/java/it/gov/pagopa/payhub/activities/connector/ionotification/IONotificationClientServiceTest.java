package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.ionotification.client.IoNotificationClient;
import it.gov.pagopa.payhub.activities.exception.IONotificationInvocationException;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import static it.gov.pagopa.payhub.activities.util.faker.NotificationRequestDTOFaker.buildNotificationRequestDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class IONotificationClientServiceTest {

    @Mock
    private IoNotificationClient ioNotificationClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private IONotificationClientServiceImpl sendIONotificationActivity;

    @BeforeEach
    void setUp() {
        sendIONotificationActivity = new IONotificationClientServiceImpl(
                ioNotificationClientMock,
                authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ioNotificationClientMock,
                authnServiceMock);
    }

    @Test
    void whenSendMessageThenInvokeClient() {
        // Given
        NotificationRequestDTO notificationRequestDTO = buildNotificationRequestDTO();
        String accessToken = "ACCESSTOKEN";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        Mockito.when(ioNotificationClientMock.sendMessage(notificationRequestDTO, accessToken))
                .thenReturn(new MessageResponseDTO("id"));

        // When
        MessageResponseDTO messageResponseDTO = sendIONotificationActivity.sendMessage(notificationRequestDTO);

        // Then
        assertNotNull(messageResponseDTO);
        assertEquals("id", messageResponseDTO.getNotificationId());
    }

    @Test
    void whenSendMessageWhenExceptionThenThrowIONotificationInvocationException() {
        // Given
        NotificationRequestDTO notificationRequestDTO = buildNotificationRequestDTO();
        String accessToken = "ACCESSTOKEN";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        Mockito.doThrow(new RestClientException("Error"))
                .when(ioNotificationClientMock).sendMessage(notificationRequestDTO, accessToken);

        // When
        IONotificationInvocationException exception =
                Assertions.assertThrows(IONotificationInvocationException.class, () -> sendIONotificationActivity.sendMessage(notificationRequestDTO));


        // Then
        assertEquals("Error", exception.getMessage());
    }
}
