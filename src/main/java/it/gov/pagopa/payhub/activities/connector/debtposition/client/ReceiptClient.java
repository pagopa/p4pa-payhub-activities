package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class ReceiptClient {
    private final DebtPositionApisHolder debtPositionApisHolder;

    public ReceiptClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public ReceiptDTO createReceipt(String accessToken, ReceiptWithAdditionalNodeDataDTO receipt) {
        return debtPositionApisHolder.getReceiptApi(accessToken).createReceipt(receipt);
    }
}
