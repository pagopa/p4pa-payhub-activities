package it.gov.pagopa.payhub.activities.connector.classification.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.PaymentNotificationApi;
import it.gov.pagopa.pu.classification.client.generated.PaymentNotificationNoPiiSearchControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationClientTest {

    @Mock
    private ClassificationApisHolder classificationApisHolderMock;

    @Mock
    private PaymentNotificationApi paymentNotificationApiMock;

    @Mock
    private PaymentNotificationNoPiiSearchControllerApi paymentNotificationNoPiiSearchControllerApi;


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

    @Test
    void givenValidOrgIdAndIudWhenGetByOrgIdAndIudThenReturnPaymentNotificationNoPII() {
        // Given
        Long organizationId = 1L;
        String iud = "IUD";
        String accessToken = "accessToken";
        PaymentNotificationNoPII expectedResponse = new PaymentNotificationNoPII();

        when(classificationApisHolderMock.getPaymentNotificationNoPiiSearchControllerApi(accessToken))
            .thenReturn(paymentNotificationNoPiiSearchControllerApi);
        when(paymentNotificationNoPiiSearchControllerApi.crudPaymentNotificationGetByOrganizationIdAndIud(organizationId, iud))
            .thenReturn(expectedResponse);

        // When
        PaymentNotificationNoPII result = paymentNotificationClient.getByOrgIdAndIud(organizationId, iud, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentNotificationNoPiiSearchControllerApi, times(1))
            .crudPaymentNotificationGetByOrganizationIdAndIud(organizationId, iud);
    }

    @Test
    void givenNotExistentPaymentNotificationWhenGetByOrgIdAndIudThenNull() {
        // Given
        Long organizationId = 1L;
        String iud = "IUD";
        String accessToken = "accessToken";

        when(classificationApisHolderMock.getPaymentNotificationNoPiiSearchControllerApi(accessToken))
            .thenReturn(paymentNotificationNoPiiSearchControllerApi);
        when(paymentNotificationNoPiiSearchControllerApi.crudPaymentNotificationGetByOrganizationIdAndIud(organizationId, iud))
            .thenThrow(
                HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        PaymentNotificationNoPII result = paymentNotificationClient.getByOrgIdAndIud(organizationId, iud, accessToken);

        // Then
        Assertions.assertNull(result);
    }
}