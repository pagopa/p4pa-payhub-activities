package it.gov.pagopa.payhub.activities.dto.treasury;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for the TreasuryIufResult, representing the result of file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryIufResult {
    /** List of extracted IUFs */
    private List<String> iufs;
    /** Treasury ID */
    private List<String> treasuryIds;
    /** Organization ID */
    private Long organizationID;
    /** Success flag for the operation */
    private boolean success;
    /** the error description */
    private String errorDescription;

    private String discardedFileName;
}
