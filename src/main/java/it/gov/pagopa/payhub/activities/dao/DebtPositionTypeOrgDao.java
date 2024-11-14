package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.DebtPositionTypeOrgDTO;

import java.util.Optional;

public interface DebtPositionTypeOrgDao {

    /**
     * It will return DebtPositionTypeOrgDTO entities authorized to the input operato
     * */
    Optional<DebtPositionTypeOrgDTO> getAuthorizedDebtPositionTypeOrgs(Long orgId, Long debtPositionTypeOrgId, String operatorUsername);}
