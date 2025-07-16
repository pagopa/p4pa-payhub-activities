package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;

/**
 * This interface provides methods that manage Receipt of debt positions within the related microservice
 */
public interface ReceiptService {
	ReceiptDTO createReceipt(ReceiptWithAdditionalNodeDataDTO receipt);
	ReceiptNoPII getByTransferId(Long transferId);
	ReceiptDTO getByReceiptId(Long receiptId);
}
