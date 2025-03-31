package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.PaymentOptionClient;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentOptionServiceTest {

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private PaymentOptionClient paymentOptionClient;

    private PaymentOptionService paymentOptionService;

    @BeforeEach
    void setUp() {
        paymentOptionService = new PaymentOptionServiceImpl(authnServiceMock, paymentOptionClient);
    }

    @Test
    void whenUpdateStatusThenInvokeClient() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long paymentOptionId = 1L;

        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

        // When
        paymentOptionService.updateStatus(paymentOptionId, PaymentOptionStatus.UNPAID);

        // Then
        verify(authnServiceMock, times(1)).getAccessToken();
        verify(paymentOptionClient, times(1)).updateStatus(paymentOptionId, PaymentOptionStatus.UNPAID, accessToken);
    }
}
