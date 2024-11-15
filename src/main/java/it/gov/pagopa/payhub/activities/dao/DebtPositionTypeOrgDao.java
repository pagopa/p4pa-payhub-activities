package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;

import java.util.Optional;

public interface DebtPositionTypeOrgDao {

    /**
     *  * It will return the requested DebtPositionTypeOrgDTO entity if authorized to the input operator
     * */
    Optional<DebtPositionTypeOrgDTO> getAuthorizedDebtPositionTypeOrg(Long orgId, Long debtPositionTypeOrgId, String operatorUsername);}
