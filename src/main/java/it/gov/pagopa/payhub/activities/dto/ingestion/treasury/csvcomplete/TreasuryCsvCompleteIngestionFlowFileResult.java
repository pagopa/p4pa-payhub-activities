package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for the Treasury csv complete, representing the result of treasury file processing.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class TreasuryCsvCompleteIngestionFlowFileResult extends TreasuryIufIngestionFlowFileResult {

    private String ipaCode;
}
