package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.PaymentsClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class PaymentsServiceImpl implements PaymentsService {

    private final PaymentsClient paymentsClient;
    private final AuthnService authnService;

    public PaymentsServiceImpl(PaymentsClient paymentsClient, AuthnService authnService) {
        this.paymentsClient = paymentsClient;
        this.authnService = authnService;
    }

    @Override
    public SendNotificationDTO notifyPayment(DebtPositionDTO debtPosition, Long receiptId) {
        String nav = debtPosition.getPaymentOptions().stream()
                .flatMap(paymentOption -> paymentOption.getInstallments().stream())
                .filter(installment -> receiptId.equals(installment.getReceiptId()))
                .findFirst()
                .map(InstallmentDTO::getNav)
                .orElse(null);

        // NAV may not be found when the receipt from the pagoPA node arrives before the one imported from a receipts file:
        // the installment stays linked to the pagoPA receipt, so no installment references the receipt imported from file.
        if (nav == null) {
            return null;
        }

        return paymentsClient.notifyPayment(debtPosition.getOrganizationId(), nav, authnService.getAccessToken());
    }
}
