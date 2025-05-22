package it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice;

import it.gov.pagopa.payhub.activities.dto.ingestion.organizationsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceRequestBody;
import org.springframework.stereotype.Service;

@Service
public class OrgSilServiceMapper {

  public OrgSilServiceRequestBody map(OrgSilServiceIngestionFlowFileDTO dto,Long organizationId) {

    return OrgSilServiceRequestBody.builder()
        .organizationId(organizationId)
        .applicationName(dto.getApplicationName())
        .serviceUrl(dto.getServiceUrl())
        .flagLegacy(dto.getFlagLegacy())
        .serviceType(dto.getServiceType())
        .build();
  }
}


