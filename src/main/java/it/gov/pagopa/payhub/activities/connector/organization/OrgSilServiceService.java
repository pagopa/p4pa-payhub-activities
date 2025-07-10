package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;

public interface OrgSilServiceService {

  OrgSilServiceDTO createOrUpdateOrgSilService(OrgSilServiceDTO orgSilServiceDTO);

}
