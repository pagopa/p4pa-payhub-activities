package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;

import java.util.Optional;

public interface OrganizationDao {

  Optional<OrganizationDTO> getOrganizationByFiscalCode(String orgFiscalCode);

  Optional<OrganizationDTO> getOrganizationByIpaCode(String ipaCode);

}
