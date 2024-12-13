package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;

import java.util.List;

public interface TreasuryDao {
     /**
      * retrieve treasury for the same iuf
      *
      * @param iuf flow unique identifier
      * @return list of treasury records associated to the iuf
      */
     List<TreasuryDTO> searchByIuf(String iuf);

}
