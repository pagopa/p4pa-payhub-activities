package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;

/**
 * Data Access Object (DAO) interface for handling Treasury data.
 */
public interface TreasuryDao {
  /**
   * find treasury data by IUV
   *
   * @param iuv    payment identifier
   * @return TreasuryDTO object returned
   */
  TreasuryDTO findByIuv(String iuv);


}
