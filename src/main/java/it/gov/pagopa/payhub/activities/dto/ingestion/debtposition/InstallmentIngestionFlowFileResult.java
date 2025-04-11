package it.gov.pagopa.payhub.activities.dto.ingestion.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for the InstallmentIngestionResult, representing the result of installment file processing.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class InstallmentIngestionFlowFileResult extends IngestionFlowFileResult {
}
