package it.gov.pagopa.payhub.activities.dto.ingestion.organizationsilservice;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import java.util.Map;
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
public class OrgSilServiceIngestionFlowFileResult extends IngestionFlowFileResult {
  private Map<String,String> orgApplicationCodeMap;
  private String brokerFiscalCode;
  private Long brokerId;
}
