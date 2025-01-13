package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

public interface DebtPositionDao {

     /**
      *  * It will save and return the new DebtPositionDTO entity
      * */
     DebtPositionDTO save(DebtPositionDTO debtPosition);
}
