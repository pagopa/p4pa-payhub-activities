package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.pu_sil.PuSilService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ResolvedInstallmentResult;
import it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptInstallmentResolverService;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link ReceiptPagopaNotifySilActivity} for sending notification to SIL of received receipt.
 * This class handles sending notification to SIL of received receipt.
 */
@Slf4j
@Lazy
@Component
public class ReceiptPagopaNotifySilActivityImpl implements ReceiptPagopaNotifySilActivity {
    private final ReceiptInstallmentResolverService receiptInstallmentResolverService;
    private final PuSilService puSilService;


    public ReceiptPagopaNotifySilActivityImpl(PuSilService puSilService,
                                              ReceiptInstallmentResolverService receiptInstallmentResolverService) {
        this.receiptInstallmentResolverService = receiptInstallmentResolverService;
        this.puSilService = puSilService;
    }

    @Override
    public void notifyReceiptToSil(ReceiptWithAdditionalNodeDataDTO receiptDTO) {
        log.info("Notify receipt to SIL by receiptId {}", receiptDTO.getReceiptId());

        ResolvedInstallmentResult resolved = receiptInstallmentResolverService.resolveInstallment(receiptDTO);

        if (resolved.isEmpty()) {
            return;
        }

        if (!resolved.getOrganization().getFlagNotifyOutcomePush()) {
            return;
        }

        resolved.getSilNotifiableInstallments().forEach(n ->
                puSilService.notifyPayment(
                        n.getDebtPositionTypeOrg().getNotifyOutcomePushOrgSilServiceId(),
                        n.getInstallment(),
                        resolved.getOrganization().getIpaCode()
                )
        );
    }
}
