package it.gov.pagopa.payhub.activities.activity.debtposition.receipt;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDTO;

@ActivityInterface
public interface ReceiptRetrieverActivity {
    @ActivityMethod
    ReceiptDTO retrieveReceipt(Long receiptId);
}
