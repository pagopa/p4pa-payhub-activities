package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;

import java.util.List;

public interface OrgSilServiceService {

  OrgSilServiceDTO createOrUpdateOrgSilService(OrgSilServiceDTO orgSilServiceDTO);

  List<OrgSilService> getAllByOrganizationIdAndServiceType(Long organizationId, OrgSilServiceType serviceType);

}
