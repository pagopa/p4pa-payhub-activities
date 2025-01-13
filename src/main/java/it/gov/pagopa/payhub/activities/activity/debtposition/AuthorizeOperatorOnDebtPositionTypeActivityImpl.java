package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.DebtPositionTypeOrgDao;
import it.gov.pagopa.payhub.activities.exception.OperatorNotAuthorizedException;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Lazy
@Service
public class AuthorizeOperatorOnDebtPositionTypeActivityImpl implements AuthorizeOperatorOnDebtPositionTypeActivity {

    private final DebtPositionTypeOrgDao debtPositionTypeOrgDao;

    public AuthorizeOperatorOnDebtPositionTypeActivityImpl(DebtPositionTypeOrgDao debtPositionTypeOrgDao) {
        this.debtPositionTypeOrgDao = debtPositionTypeOrgDao;
    }

    public DebtPositionTypeOrg authorize(Long orgId, Long debtPositionTypeOrgId, String username){
        Optional<DebtPositionTypeOrg> debtPositionTypeOrg =
                debtPositionTypeOrgDao.getAuthorizedDebtPositionTypeOrg(orgId, debtPositionTypeOrgId, username);

        return debtPositionTypeOrg
                .orElseThrow(() -> new OperatorNotAuthorizedException("The operator " + username + " is not authorized on the DebtPositionTypeOrg " + debtPositionTypeOrgId + " related to organization " + orgId));
    }
}
