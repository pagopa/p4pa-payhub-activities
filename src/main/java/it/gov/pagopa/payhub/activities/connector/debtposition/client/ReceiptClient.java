package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Lazy
@Slf4j
@Service
public class ReceiptClient {
    private final DebtPositionApisHolder debtPositionApisHolder;

    public ReceiptClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public ReceiptDTO createReceipt(String accessToken, ReceiptWithAdditionalNodeDataDTO receipt) {
        return debtPositionApisHolder.getReceiptApi(accessToken).createReceipt(receipt);
    }

    public ReceiptNoPII getByTransferId(String accessToken, Long transferId) {
        try {
            return debtPositionApisHolder.getReceiptNoPiiSearchControllerApi(accessToken)
                .crudReceiptsGetByTransferId(transferId);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("ReceiptDTO not found for TransferId: {}", transferId);
            return null;
        }
    }

   public ReceiptDTO getByReceiptId(String accessToken, Long receiptId) {
        try {
           return debtPositionApisHolder.getReceiptApi(accessToken)
                   .getReceipt(receiptId);
       } catch (HttpClientErrorException.NotFound e) {
           log.info("ReceiptDTO not found for id: {}", receiptId);
           return null;
       }
    }

    public ReceiptNoPII getByPaymentReceiptId(String accessToken, String paymentReceiptId) {
        try {
            return debtPositionApisHolder.getReceiptNoPiiSearchControllerApi(accessToken)
                    .crudReceiptsGetByPaymentReceiptId(paymentReceiptId);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Receipt not found for paymentReceiptId: {}", paymentReceiptId);
            return null;
        }
    }
}
