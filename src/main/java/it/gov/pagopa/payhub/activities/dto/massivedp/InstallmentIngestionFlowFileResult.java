package it.gov.pagopa.payhub.activities.dto.massivedp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for the InstallmentIngestionResult, representing the result of installment file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentIngestionFlowFileResult {
    /** Map of IUDs and their corresponding Installments IDs */
    private Map<String, String> iud2InstallmentsIdMap;
    /** Error description */
    private String errorDescription;
    /** Discarded file name */
    private String discardedFileName;
}
