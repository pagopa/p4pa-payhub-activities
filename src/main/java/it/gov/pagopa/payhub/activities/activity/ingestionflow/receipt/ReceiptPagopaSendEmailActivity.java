package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;

/**
 * Interface for ReceiptPagopaSendEmailActivity.
 * If the received receipt is linked to a ordinary installment, send email to citizen.
 */
@ActivityInterface
public interface ReceiptPagopaSendEmailActivity {

    /**
     * Sends an email to citizen.
     *
     * @param receiptDTO the received receipt.
     * @param installmentDTO the "ordinary" installment associated to the receipt.
     */
    @ActivityMethod
    void sendReceiptHandledEmail(ReceiptWithAdditionalNodeDataDTO receiptDTO, InstallmentDTO installmentDTO);
}
