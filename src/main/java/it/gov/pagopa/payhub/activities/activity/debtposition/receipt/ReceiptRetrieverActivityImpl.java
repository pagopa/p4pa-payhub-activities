package it.gov.pagopa.payhub.activities.activity.debtposition.receipt;

import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class ReceiptRetrieverActivityImpl implements ReceiptRetrieverActivity {

    private final ReceiptService receiptService;

    public ReceiptRetrieverActivityImpl(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @Override
    public ReceiptDTO retrieveReceipt(Long receiptId) {
        log.info("Retrieve receipt having receiptId {}", receiptId);
        return receiptService.getByReceiptId(receiptId);
    }
}
