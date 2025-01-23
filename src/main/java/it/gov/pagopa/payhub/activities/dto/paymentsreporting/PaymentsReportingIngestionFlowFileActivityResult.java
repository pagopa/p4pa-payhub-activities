package it.gov.pagopa.payhub.activities.dto.paymentsreporting;

import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for the PaymentsReportingIngestionFlowFileActivityResult, representing the result of file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsReportingIngestionFlowFileActivityResult {
    /** Map of IUFs and their corresponding TransferSemanticKeyDTO */
    private Map<String, TransferSemanticKeyDTO> transferSemanticKeys;
    /** Success flag for the operation */
    private boolean success;
    /** the error description */
    private String errorDescription;
}
