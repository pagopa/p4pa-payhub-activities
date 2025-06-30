package it.gov.pagopa.payhub.activities.dto.ingestion.organization;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class OrganizationIngestionFlowFileResult extends IngestionFlowFileResult {
    private String brokerFiscalCode;
    private Long brokerId;
}
