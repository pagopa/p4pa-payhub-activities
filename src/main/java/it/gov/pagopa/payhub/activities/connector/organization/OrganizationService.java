package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {

  Optional<Organization> getOrganizationByFiscalCode(String orgFiscalCode);

  Optional<Organization> getOrganizationByIpaCode(String ipaCode);

  Optional<Organization> getOrganizationById(Long organizationId);

  List<Organization> getActiveOrganizationsByBrokerId(Long brokerId);

  Organization createOrganization(OrganizationCreateDTO organization);

}
