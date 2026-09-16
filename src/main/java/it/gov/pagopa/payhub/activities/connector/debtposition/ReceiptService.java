package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptWithAdditionalNodeDataDTO;

/**
 * This interface provides methods that manage Receipt of debt positions within the related microservice
 */
public interface ReceiptService {
	ReceiptDTO createReceipt(ReceiptWithAdditionalNodeDataDTO receipt);
	ReceiptNoPII getByTransferId(Long transferId);
	ReceiptDTO getByReceiptId(Long receiptId);
	ReceiptNoPII getByPaymentReceiptId(String paymentReceiptId);
	FileResourceDTO getReceiptPdf(Long receiptId, Long organizationId);
}
