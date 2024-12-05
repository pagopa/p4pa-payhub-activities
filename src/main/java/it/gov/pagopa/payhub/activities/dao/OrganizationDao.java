package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;

public interface OrganizationDao {

    OrganizationDTO getById(Long id);
}
