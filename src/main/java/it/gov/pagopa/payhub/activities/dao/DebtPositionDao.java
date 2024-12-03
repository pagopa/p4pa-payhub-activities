package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;

public interface DebtPositionDao {

    long save(DebtPositionDTO debtPosition);
}
