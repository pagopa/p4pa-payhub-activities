package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for the Treasury csv, representing the result of treasury file processing.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class TreasuryCsvIngestionFlowFileResult extends TreasuryIufIngestionFlowFileResult {

    private String ipaCode;
}
