package it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class DebtPositionTypeOrgIngestionFlowFileResult extends IngestionFlowFileResult {
  private Long brokerId;
  private String orgIpaCode;
}
