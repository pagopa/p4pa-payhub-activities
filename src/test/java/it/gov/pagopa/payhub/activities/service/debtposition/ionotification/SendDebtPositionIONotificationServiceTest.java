package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.ionotification.IoNotificationClient;
import it.gov.pagopa.pu.p4paionotification.dto.generated.NotificationQueueDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SendDebtPositionIONotificationServiceTest {
    @Mock
    private IoNotificationClient ioNotificationClientMock;

    private SendDebtPositionIONotificationServiceImpl sendIONotificationActivity;

    @BeforeEach
    void setUp() {
        sendIONotificationActivity = new SendDebtPositionIONotificationServiceImpl(ioNotificationClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(ioNotificationClientMock);
    }

    @Test
    void whenSendMessageThenInvokeClient() {
        // Given
        NotificationQueueDTO notificationQueueDTO = new NotificationQueueDTO();

        // When
        sendIONotificationActivity.sendMessage(notificationQueueDTO);

        // Then
        Mockito.verify(ioNotificationClientMock).sendMessage(notificationQueueDTO);
    }
}
