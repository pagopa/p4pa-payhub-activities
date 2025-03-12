package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {

  Optional<Organization> getOrganizationByFiscalCode(String orgFiscalCode);

  Optional<Organization> getOrganizationByIpaCode(String ipaCode);

  Optional<Organization> getOrganizationById(Long organizationId);

  List<Organization> getOrganizationsByBrokerId(Long brokerId);

}
