package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceRequestBody;

public interface OrgSilServiceService {

  OrgSilService createOrgSilService(OrgSilServiceRequestBody orgSilServiceRequestBody);

}
