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
public class TreasuryIufResult {
    /** Map of IUFs and their corresponding Treasury IDs */
    private Map<String, String> iufTreasuryIdMap;
    /** Organization ID */
    private Long organizationId;
    /** Success flag for the operation */
    private boolean success;
    /** Error description */
    private String errorDescription;
    /** Discarded file name */
    private String discardedFileName;
}
