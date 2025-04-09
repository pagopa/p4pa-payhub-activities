package it.gov.pagopa.payhub.activities.connector.classification.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.PaymentNotificationApi;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationClientTest {

    @Mock
    private ClassificationApisHolder classificationApisHolderMock;

    @Mock
    private PaymentNotificationApi paymentNotificationApiMock;

    private PaymentNotificationClient paymentNotificationClient;

    @BeforeEach
    void setUp() {
        paymentNotificationClient = new PaymentNotificationClient(classificationApisHolderMock);
    }

    @Test
    void testCreatePaymentNotification() {
        // Given
        PaymentNotificationDTO dto = new PaymentNotificationDTO();
        String accessToken = "accessToken";
        PaymentNotificationDTO expectedResponse = new PaymentNotificationDTO();

        when(classificationApisHolderMock.getPaymentNotificationApi(accessToken))
                .thenReturn(paymentNotificationApiMock);
        when(paymentNotificationApiMock.createPaymentNotification(dto)).thenReturn(expectedResponse);

        when(classificationApisHolderMock.getPaymentNotificationApi(accessToken))
                .thenReturn(paymentNotificationApiMock);
        when(paymentNotificationApiMock.createPaymentNotification(dto)).thenReturn(expectedResponse);

        // When
        PaymentNotificationDTO result = paymentNotificationClient.createPaymentNotification(dto, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentNotificationApiMock, times(1)).createPaymentNotification(dto);
    }

}