package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.ionotification.client.IoNotificationClient;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationQueueMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationQueueDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.NotificationQueueFaker.buildNotificationQueueDTO;

@ExtendWith(MockitoExtension.class)
class IONotificationServiceTest {

    @Mock
    private IoNotificationClient ioNotificationClientMock;
    @Mock
    private NotificationQueueMapper notificationQueueMapperMock;
    @Mock
    private AuthnService authnServiceMock;

    private IONotificationServiceImpl sendIONotificationActivity;

    @BeforeEach
    void setUp() {
        sendIONotificationActivity = new IONotificationServiceImpl(
                ioNotificationClientMock,
                notificationQueueMapperMock,
                authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ioNotificationClientMock,
                notificationQueueMapperMock,
                authnServiceMock);
    }

    @Test
    void whenSendMessageThenInvokeClient() {
        // Given
        DebtPositionDTO debtPosition = buildDebtPositionDTO();
        NotificationQueueDTO notificationQueueDTO = buildNotificationQueueDTO();
        String accessToken = "ACCESSTOKEN";

        Mockito.when(notificationQueueMapperMock.mapDebtPositionDTO2NotificationQueueDTO(debtPosition))
                .thenReturn(List.of(notificationQueueDTO));
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        sendIONotificationActivity.sendMessage(debtPosition);

        // Then
        Mockito.verify(ioNotificationClientMock).sendMessage(notificationQueueDTO, accessToken);
    }
}
