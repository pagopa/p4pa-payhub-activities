package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;


public interface TreasuryDao {

  Long insert(TreasuryDTO treasuryDto);

  int deleteByIdEnteAndCodBollettaAndAnnoBolletta(Long id, String codBolletta, String annoBolletta);

  TreasuryDTO getByIdEnteAndCodBollettaAndAnnoBolletta(Long idEnte, String codBolletta, String annoBolletta);









}
