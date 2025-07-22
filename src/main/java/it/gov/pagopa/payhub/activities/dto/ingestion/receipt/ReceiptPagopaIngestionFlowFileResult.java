package it.gov.pagopa.payhub.activities.dto.ingestion.receipt;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for the ReceiptPagopaResult, representing the result of receipt file processing.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ReceiptPagopaIngestionFlowFileResult extends IngestionFlowFileResult {
    /** Receipt */
    private ReceiptWithAdditionalNodeDataDTO receiptDTO;
}
