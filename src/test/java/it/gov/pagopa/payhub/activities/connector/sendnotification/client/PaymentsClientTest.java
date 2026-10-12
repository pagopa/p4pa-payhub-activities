package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.client.generated.PaymentsApi;
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
class PaymentsClientTest {

    @Mock
    private SendApisHolder sendApisHolderMock;

    @Mock
    private PaymentsApi paymentsApiMock;

    private PaymentsClient paymentsClient;

    @BeforeEach
    void init() {
        paymentsClient = new PaymentsClient(sendApisHolderMock);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(sendApisHolderMock, paymentsApiMock);
    }

    @Test
    void whenNotifyPaymentThenOk() {
        Long organizationId = 1L;
        String nav = "NAV123";
        String accessToken = "accessToken";

        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        when(sendApisHolderMock.getPaymentsApi(accessToken))
                .thenReturn(paymentsApiMock);

        when(paymentsApiMock.notifyPayment(organizationId, nav))
                .thenReturn(expectedResponse);

        SendNotificationDTO result = assertDoesNotThrow(() -> paymentsClient.notifyPayment(organizationId, nav, accessToken));

        assertSame(expectedResponse, result);
    }
}
