package it.gov.pagopa.payhub.activities.activity.sendnotification.payments;

import it.gov.pagopa.payhub.activities.connector.sendnotification.PaymentsService;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotifyPaymentActivityTest {

    @Mock
    private PaymentsService paymentsServiceMock;

    private NotifyPaymentActivity notifyPaymentActivity;

    @BeforeEach
    void init() {
        notifyPaymentActivity = new NotifyPaymentActivityImpl(paymentsServiceMock);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(paymentsServiceMock);
    }

    @Test
    void whenNotifyPaymentThenOk() {
        DebtPositionDTO debtPosition = new DebtPositionDTO();
        Long receiptId = 1L;
        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        when(paymentsServiceMock.notifyPayment(debtPosition, receiptId))
                .thenReturn(expectedResponse);

        SendNotificationDTO result = assertDoesNotThrow(() -> notifyPaymentActivity.notifyPayment(debtPosition, receiptId));

        assertSame(expectedResponse, result);
    }
}
