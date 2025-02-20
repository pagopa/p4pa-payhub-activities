package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.ReceiptClient;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class ReceiptServiceImpl implements ReceiptService {
    private final AuthnService authnService;
    private final ReceiptClient receiptClient;

    public ReceiptServiceImpl(AuthnService authnService, ReceiptClient receiptClient) {
        this.authnService = authnService;
        this.receiptClient = receiptClient;
    }

    @Override
    public ReceiptDTO createReceipt(ReceiptWithAdditionalNodeDataDTO receipt) {
        return receiptClient.createReceipt(authnService.getAccessToken(), receipt);
    }
}
