package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class TreasuryPosteIngestionFlowFileResult extends TreasuryIufIngestionFlowFileResult {

    private String iban;
}
