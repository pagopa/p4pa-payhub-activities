package it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class OrgSilServiceIngestionFlowFileResult extends IngestionFlowFileResult {

    private String ipaCode;

}

