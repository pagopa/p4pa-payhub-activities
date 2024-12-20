package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dao.OrganizationDao;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class exposing methods related to Organization entity.
 */
@Lazy
@Service
public class OrganizationService {

  private final OrganizationDao organizationDao;

  public OrganizationService(OrganizationDao organizationDao) {
    this.organizationDao = organizationDao;
  }

  /*
   * Retrieve organization entity by fiscal code.
   * @param orgFiscalCode the fiscal code of the organization to retrieve
   * @return the retrieved organization
   */
  public Optional<OrganizationDTO> getOrganizationByFiscalCode(String orgFiscalCode){
    return organizationDao.getOrganizationByFiscalCode(orgFiscalCode);
  }

  public OrganizationDTO getOrganizationByIpaCode(String ipaCode){
    return organizationDao.getOrganizationByIpaCode(ipaCode);
  }


}
