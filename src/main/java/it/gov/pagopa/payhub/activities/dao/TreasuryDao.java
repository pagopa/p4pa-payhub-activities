package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;


public interface TreasuryDao {

  Long insert(TreasuryDTO treasuryDto);

  int deleteByOrganizationIdAndBillCodeAndBillYear(Long organizationId, String billCode, String billYear);

  TreasuryDTO getByOrganizationIdAndBillCodeAndBillYear(Long organizationId, String billCode, String billYear);









}
