package it.gov.pagopa.payhub.activities.dto.ingestion.debtposition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the InstallmentIngestionResult, representing the result of installment file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentIngestionFlowFileResult {
    /** The total number of rows in the file */
    private Long totalRows;
    /** The number of rows correctly handled */
    private Long processedRows;
    /** Error description */
    private String errorDescription;
    /** Discarded file name */
    private String discardedFileName;
}
