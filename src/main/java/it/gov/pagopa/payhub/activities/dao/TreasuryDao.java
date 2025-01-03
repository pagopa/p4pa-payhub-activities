package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;


public interface TreasuryDao {

  /**
   * search for treasury of a specific organization associated to a payment reporting identifier
   *
   * @param organizationId  organization id
   * @param iuf payment reporting identifier
   * @return TreasuryDTO object containing treasury data
   */
  TreasuryDTO getByOrganizationIdAndIuf(Long organizationId, String iuf);

}
