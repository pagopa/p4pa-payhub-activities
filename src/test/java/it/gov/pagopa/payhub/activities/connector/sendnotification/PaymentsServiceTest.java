package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.PaymentsClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentsServiceTest {

    @Mock
    private PaymentsClient paymentsClientMock;

    @Mock
    private AuthnService authnServiceMock;

    private PaymentsService paymentsService;

    @BeforeEach
    void init() {
        paymentsService = new PaymentsServiceImpl(paymentsClientMock, authnServiceMock);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(paymentsClientMock, authnServiceMock);
    }

    @Test
    void whenNotifyPaymentThenOk() {
        Long organizationId = 1L;
        Long receiptId = 2L;
        String nav = "NAV123";
        String accessToken = "accessToken";

        InstallmentDTO installment = new InstallmentDTO();
        installment.setReceiptId(receiptId);
        installment.setNav(nav);

        PaymentOptionDTO paymentOption = new PaymentOptionDTO();
        paymentOption.setInstallments(List.of(installment));

        DebtPositionDTO debtPosition = new DebtPositionDTO();
        debtPosition.setOrganizationId(organizationId);
        debtPosition.setPaymentOptions(List.of(paymentOption));

        SendNotificationDTO expectedResponse = new SendNotificationDTO();

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        when(paymentsClientMock.notifyPayment(organizationId, nav, accessToken))
                .thenReturn(expectedResponse);

        SendNotificationDTO result = assertDoesNotThrow(() -> paymentsService.notifyPayment(debtPosition, receiptId));

        assertSame(expectedResponse, result);
    }

    @Test
    void whenNotifyPaymentWithInvalidReceiptIdThenThrowException() {
        Long receiptId = 2L;

        InstallmentDTO installment = new InstallmentDTO();
        installment.setReceiptId(3L);
        installment.setNav("NAV123");

        PaymentOptionDTO paymentOption = new PaymentOptionDTO();
        paymentOption.setInstallments(List.of(installment));

        DebtPositionDTO debtPosition = new DebtPositionDTO();
        debtPosition.setPaymentOptions(List.of(paymentOption));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> paymentsService.notifyPayment(debtPosition, receiptId));

        assertEquals("NAV not found for receiptId: " + receiptId, exception.getMessage());
    }
}