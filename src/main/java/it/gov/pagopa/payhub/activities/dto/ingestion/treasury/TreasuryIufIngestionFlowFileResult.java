package it.gov.pagopa.payhub.activities.dto.ingestion.treasury;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * DTO for the TreasuryIufResult, representing the result of treasury file processing.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TreasuryIufIngestionFlowFileResult extends IngestionFlowFileResult {
    /** Map of IUFs and their corresponding Treasury IDs */
    private Map<String, String> iuf2TreasuryIdMap;
    /** Organization ID */
    private Long organizationId;
}
