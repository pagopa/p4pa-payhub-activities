package it.gov.pagopa.payhub.activities.connector.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.PaymentNotificationClient;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationServiceTest {

    @Mock
    private PaymentNotificationClient paymentNotificationClientMock;
    @Mock
    private AuthnService authnServiceMock;
    private PaymentNotificationServiceImpl paymentNotificationService;

    @BeforeEach
    void setUp() {
        paymentNotificationService = new PaymentNotificationServiceImpl(
            paymentNotificationClientMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
            paymentNotificationClientMock,
                authnServiceMock);
    }

    @Test
    void testCreatePaymentNotification() {
        // Given
        PaymentNotificationDTO dto = new PaymentNotificationDTO();
        String accessToken = "accessToken";
        PaymentNotificationDTO expectedResponse = new PaymentNotificationDTO();

        when(paymentNotificationClientMock.createPaymentNotification(dto, accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        PaymentNotificationDTO result = paymentNotificationService.createPaymentNotification(dto);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentNotificationClientMock, times(1)).createPaymentNotification(dto, accessToken);
    }

}