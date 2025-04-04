package it.gov.pagopa.payhub.activities.dto.receipt;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the ReceiptPagopaResult, representing the result of receipt file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptPagopaIngestionFlowFileResult {
    /** Receipt */
    private ReceiptWithAdditionalNodeDataDTO receiptDTO;
    /** Ordinary installment mapped to receipt, if found */
    private InstallmentDTO installmentDTO;
}
