package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;

/**
 * Interface for the ReceiptPagopaNotifySilActivity.
 * Defines methods for sending notification to sil after a pagopa receipt has been received.
 */
@ActivityInterface
public interface ReceiptPagopaNotifySilActivity {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param receiptDTO the received receipt.
     */
    @ActivityMethod
    void notifyReceiptToSil(ReceiptWithAdditionalNodeDataDTO receiptDTO);
}