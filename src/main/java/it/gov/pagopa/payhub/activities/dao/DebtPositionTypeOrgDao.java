package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;

import java.util.Optional;

public interface DebtPositionTypeOrgDao {

    /**
     *  * It will return the requested DebtPositionTypeOrgDTO entity if authorized to the input operator
     * */
    Optional<DebtPositionTypeOrg> getAuthorizedDebtPositionTypeOrg(Long orgId, Long debtPositionTypeOrgId, String operatorUsername);}
