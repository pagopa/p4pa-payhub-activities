package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationStatus;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationPaymentsDTO;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationStatusActivityTest {

    @Mock
    private SendService sendServiceMock;
    @Mock
    private SendNotificationService sendNotificationServiceMock;
    @Mock
    private InstallmentService installmentServiceMock;

    private NotificationStatusActivity notificationStatusActivity;

    @BeforeEach
    void init() {
        notificationStatusActivity = new NotificationStatusActivityImpl(
                sendServiceMock,
                sendNotificationServiceMock,
                installmentServiceMock);
    }

    @Test
    void whenSendNotificationStatusThenOk() {
        // Given
        String notificationId = "sendNotificationId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        Mockito.when(sendServiceMock.notificationStatus(notificationId))
                .thenReturn(expectedResponse);

        // When
        SendNotificationDTO result = notificationStatusActivity.getSendNotificationStatus(notificationId);

        // Then
        assertSame(expectedResponse, result);
        Mockito.verify(sendServiceMock).notificationStatus(notificationId);
        Mockito.verifyNoInteractions(sendNotificationServiceMock);
    }

    @Test
    void givenAllDataPresentWhenSendNotificationStatusThenVerifyUpdatesInstallmentIun() {
        // Given
        String sendNotificationId = "sendNotificationId";
        String iun = "IUN";
        Long debtPositionId = 1L;

        SendNotificationPaymentsDTO notificationPayment = new SendNotificationPaymentsDTO();
        notificationPayment.setDebtPositionId(debtPositionId);

        SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
        sendNotificationDTO.setIun(iun);
        sendNotificationDTO.setPayments(List.of(notificationPayment));

        Mockito.when(sendServiceMock.notificationStatus(sendNotificationId))
                .thenReturn(sendNotificationDTO);

        // When
        SendNotificationDTO result = notificationStatusActivity.getSendNotificationStatus(sendNotificationId);

        // Then
        assertSame(sendNotificationDTO, result);
        Mockito.verify(sendServiceMock).notificationStatus(sendNotificationId);
        Mockito.verify(installmentServiceMock).updateIunByDebtPositionId(debtPositionId, iun);
        Mockito.verifyNoInteractions(sendNotificationServiceMock);
    }

    @Test
    void givenSendNotificationDTOIsNullWhenSendNotificationStatusThenDoNothing() {
        // Given
        String sendNotificationId = "sendNotificationId";
        Mockito.when(sendServiceMock.notificationStatus(sendNotificationId)).thenReturn(null);

        // When
        SendNotificationDTO result = notificationStatusActivity.getSendNotificationStatus(sendNotificationId);

        // Then
        assertNull(result);
        Mockito.verify(sendServiceMock).notificationStatus(sendNotificationId);
        Mockito.verifyNoInteractions(installmentServiceMock);
        Mockito.verifyNoInteractions(sendNotificationServiceMock);
    }

    @Test
    void givenIunIsNullWhenSendNotificationStatusThenDoNothing() {
        // Given
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
        sendNotificationDTO.setIun(null);

        Mockito.when(sendServiceMock.notificationStatus(sendNotificationId))
                .thenReturn(sendNotificationDTO);

        // When
        SendNotificationDTO result = notificationStatusActivity.getSendNotificationStatus(sendNotificationId);

        // Then
        assertSame(sendNotificationDTO, result);
        Mockito.verify(sendServiceMock).notificationStatus(sendNotificationId);
        Mockito.verifyNoInteractions(installmentServiceMock);
        Mockito.verifyNoInteractions(sendNotificationServiceMock);
    }

    @Test
    void givenExceptionWhenSendNotificationStatusThenUpdateStatusToErrorAndRethrow() {
        // Given
        String sendNotificationId = "sendNotificationId";
        RuntimeException ex = new RuntimeException("Service error");

        Mockito.when(sendServiceMock.notificationStatus(sendNotificationId))
                .thenThrow(ex);

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> notificationStatusActivity.getSendNotificationStatus(sendNotificationId));

        assertEquals("Service error", thrown.getMessage());

        Mockito.verify(sendNotificationServiceMock)
                .updateNotificationStatus(sendNotificationId, NotificationStatus.ERROR.getValue());
    }
}
