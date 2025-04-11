package it.gov.pagopa.payhub.activities.dto.ingestion.receipt;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO for the ReceiptPagopaResult, representing the result of receipt file processing.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReceiptPagopaIngestionFlowFileResult extends IngestionFlowFileResult {
    /** Receipt */
    private ReceiptWithAdditionalNodeDataDTO receiptDTO;
    /** Ordinary installment mapped to receipt, if found */
    private InstallmentDTO installmentDTO;
}
