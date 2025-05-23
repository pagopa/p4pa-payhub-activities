package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.IngestionFlowFileProcessorActivity;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptPagopaIngestionFlowFileResult;

/**
 * Interface for the ReceiptPagopaIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
@ActivityInterface
public interface ReceiptPagopaIngestionActivity extends IngestionFlowFileProcessorActivity<ReceiptPagopaIngestionFlowFileResult> {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowFileId the unique identifier related to the file to process.
     * @return {@link ReceiptPagopaIngestionFlowFileResult} containing the receiptDto and the mapped "ordinary" installment (if present).
     */
    @ActivityMethod
    @Override
    ReceiptPagopaIngestionFlowFileResult processFile(Long ingestionFlowFileId);
}