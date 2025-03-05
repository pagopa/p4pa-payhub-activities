package it.gov.pagopa.payhub.activities.connector.ionotification.client;

import it.gov.pagopa.payhub.activities.connector.ionotification.config.IoNotificationApisHolder;
import it.gov.pagopa.pu.ionotification.client.generated.IoNotificationApi;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IoNotificationClientTest {
    @Mock
    private IoNotificationApisHolder ioNotificationApisHolder;
    @Mock
    private IoNotificationApi ioNotificationApi;

    private IoNotificationClient ioNotificationClient;

    @BeforeEach
    void setUp() {
        ioNotificationClient = new IoNotificationClient(ioNotificationApisHolder);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                ioNotificationApisHolder
        );
    }

    @Test
    void whenSendMessageThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        NotificationRequestDTO request = new NotificationRequestDTO();

        Mockito.when(ioNotificationApisHolder.getIoNotificationApi(accessToken))
                .thenReturn(ioNotificationApi);

        // When
        ioNotificationClient.sendMessage(request, accessToken);

        // Then
        Mockito.verify(ioNotificationApi)
                .sendMessage(request);
    }
}
