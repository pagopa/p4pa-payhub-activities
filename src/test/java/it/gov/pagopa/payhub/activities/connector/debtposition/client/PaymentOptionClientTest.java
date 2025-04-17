package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.PaymentOptionEntityExtendedControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentOptionClientTest {

    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    PaymentOptionEntityExtendedControllerApi paymentOptionEntityExtendedControllerApiMock;

    @InjectMocks
    private PaymentOptionClient paymentOptionClient;

    @Test
    void whenUpdateStatusThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long paymentOptionId = 1L;

        when(debtPositionApisHolderMock.getPaymentOptionEntityExtendedControllerApi(accessToken))
                .thenReturn(paymentOptionEntityExtendedControllerApiMock);

        // When
        paymentOptionClient.updateStatus(paymentOptionId, PaymentOptionStatus.UNPAID, accessToken);

        // Then
        verify(debtPositionApisHolderMock, times(1)).getPaymentOptionEntityExtendedControllerApi(accessToken);
        verify(paymentOptionEntityExtendedControllerApiMock, times(1)).updateStatus(paymentOptionId, PaymentOptionStatus.UNPAID);
    }
}
