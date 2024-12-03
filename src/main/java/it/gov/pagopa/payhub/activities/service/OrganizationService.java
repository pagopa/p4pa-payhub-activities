package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dao.OrganizationDao;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class OrganizationService {

  private final OrganizationDao organizationDao;

  public OrganizationService(OrganizationDao organizationDao) {
    this.organizationDao = organizationDao;
  }

  public OrganizationDTO getOrganizationByFiscalCode(String orgFiscalCode){
    return organizationDao.getOrganizationByFiscalCode(orgFiscalCode);
  }
}
