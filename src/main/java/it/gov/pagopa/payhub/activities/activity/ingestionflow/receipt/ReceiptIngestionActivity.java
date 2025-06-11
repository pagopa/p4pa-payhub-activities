package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileResult;

/**
 * Interface for the ReceiptIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
@ActivityInterface
public interface ReceiptIngestionActivity {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowFileId the unique identifier related to the file to process.
     * @return {@link ReceiptIngestionFlowFileResult} containing the receiptDto, the number of total rows and the number of row correctly handled.
     */
    @ActivityMethod(name = "ProcessReceiptFile")
    ReceiptIngestionFlowFileResult processFile(Long ingestionFlowFileId);
}
