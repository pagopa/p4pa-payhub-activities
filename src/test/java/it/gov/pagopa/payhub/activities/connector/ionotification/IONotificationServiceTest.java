package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.ionotification.client.IoNotificationClient;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationRequestMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.NotificationRequestDTOFaker.buildNotificationRequestDTO;

@ExtendWith(MockitoExtension.class)
class IONotificationServiceTest {

    @Mock
    private IoNotificationClient ioNotificationClientMock;
    @Mock
    private NotificationRequestMapper notificationRequestMapperMock;
    @Mock
    private AuthnService authnServiceMock;

    private IONotificationServiceImpl sendIONotificationActivity;

    @BeforeEach
    void setUp() {
        sendIONotificationActivity = new IONotificationServiceImpl(
                ioNotificationClientMock,
                notificationRequestMapperMock,
                authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ioNotificationClientMock,
                notificationRequestMapperMock,
                authnServiceMock);
    }

    @Test
    void whenSendMessageThenInvokeClient() {
        // Given
        DebtPositionDTO debtPosition = buildDebtPositionDTO();
        NotificationRequestDTO notificationRequestDTO = buildNotificationRequestDTO();
        String accessToken = "ACCESSTOKEN";
        String serviceId = "serviceId";
        String subject = "subject";
        String markdown = "markdown";

        Mockito.when(notificationRequestMapperMock.map(debtPosition, serviceId, subject, markdown))
                .thenReturn(List.of(notificationRequestDTO));
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        sendIONotificationActivity.sendMessage(debtPosition);

        // Then
        Mockito.verify(ioNotificationClientMock).sendMessage(notificationRequestDTO, accessToken);
    }
}
