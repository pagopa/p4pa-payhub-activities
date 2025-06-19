package it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class DebtPositionTypeOrgIngestionFlowFileResult extends IngestionFlowFileResult {
  private List<String> debtPositionTypeCodeList;
  private String brokerFiscalCode;
  private Long brokerId;
}
