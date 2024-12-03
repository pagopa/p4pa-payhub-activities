package it.gov.pagopa.payhub.activities.dto.reportingflow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for the PaymentsReportingIngestionFlowActivityResult, representing the result of file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsReportingIngestionFlowActivityResult {
    /** List of extracted IUFs */
    private List<String> iufs;
    /** Success flag for the operation */
    private boolean success;
}
