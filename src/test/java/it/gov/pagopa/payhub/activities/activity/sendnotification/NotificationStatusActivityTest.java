package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationPaymentsDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class NotificationStatusActivityTest {

    @Mock
    private SendService sendServiceMock;
    @Mock
    private InstallmentService installmentServiceMock;

    private NotificationStatusActivity notificationStatusActivity;

    @BeforeEach
    void init() {
        notificationStatusActivity = new NotificationStatusActivityImpl(
            sendServiceMock,
            installmentServiceMock);
    }


    @Test
    void whenSendNotificationStatusThenOk() {
        // Given
        String notificationId = "sendNotificationId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        // When
        Mockito.when(sendServiceMock.notificationStatus(notificationId)).thenReturn(expectedResponse);

        SendNotificationDTO result = notificationStatusActivity.getSendNotificationStatus("sendNotificationId");

        // Then
        assertSame(expectedResponse, result);
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

        Mockito.when(sendServiceMock.notificationStatus(sendNotificationId)).thenReturn(sendNotificationDTO);

        // When
        SendNotificationDTO result = notificationStatusActivity.getSendNotificationStatus(sendNotificationId);

        // Then
        assertSame(sendNotificationDTO, result);
        Mockito.verify(sendServiceMock).notificationStatus(sendNotificationId);
        Mockito.verify(installmentServiceMock).updateIunByDebtPositionId(debtPositionId, iun);
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
    }

    @Test
    void givenIunIsNullWhenSendNotificationStatusThenDoNothing() {
        // Given
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
        sendNotificationDTO.setIun(null);
        Mockito.when(sendServiceMock.notificationStatus(sendNotificationId)).thenReturn(sendNotificationDTO);
        // When
        SendNotificationDTO result = notificationStatusActivity.getSendNotificationStatus(sendNotificationId);
        // Then
        assertSame(sendNotificationDTO, result);
        Mockito.verify(sendServiceMock).notificationStatus(sendNotificationId);
        Mockito.verifyNoInteractions(installmentServiceMock);
    }
}