package it.gov.pagopa.payhub.activities.connector.workflowhub.dto;

import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.WfExecutionConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class WfExecutionParameters {
  private boolean massive;
  private boolean partialChange;
  private WfExecutionConfig wfExecutionConfig;
}
