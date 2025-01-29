package it.gov.pagopa.payhub.activities.dto.treasury;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for the TreasuryIufResult, representing the result of treasury file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryIufIngestionFlowFileResult {
    /** Map of IUFs and their corresponding Treasury IDs */
    private Map<String, String> iuf2TreasuryIdMap;
    /** Organization ID */
    private Long organizationId;
    /** Error description */
    private String errorDescription;
    /** Discarded file name */
    private String discardedFileName;
}
