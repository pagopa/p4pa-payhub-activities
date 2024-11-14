package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.DeptPositionTypeOrgDao;
import it.gov.pagopa.payhub.activities.dto.DebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.exception.custom.OperatorNotAuthorizedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for verifying authorization on the DebtPositionType entity.
 * This class provides methods to verify whether an operator has authorization to manage installment types
 * associated with a particular organization.
 */

@Service
public class AuthorizeOperatorOnDebtPositionTypeActivity {

    private final DeptPositionTypeOrgDao deptPositionTypeOrgDao;

    /**
     * Constructs a new {@code VerifyAuthorizationInstallmentsActivity} instance.
     *
     * @param deptPositionTypeOrgDao the data access object for {@code DebtPositionTypeOrg} entities
     */

    public AuthorizeOperatorOnDebtPositionTypeActivity(DeptPositionTypeOrgDao deptPositionTypeOrgDao) {
        this.deptPositionTypeOrgDao = deptPositionTypeOrgDao;
    }

    /**
     * Verifies if the specified operator has authorization to manage a specific installment type for an organization.
     * <p>
     * This method retrieves an authorized {@link DebtPositionTypeOrgDTO} associated with the specified organization
     * and checks if the operator is authorized to manage it based on the provided {@code orgId},
     * {@code debtPositionTypeOrgId}, and {@code username}.
     * </p>
     *
     * @param orgId the identifier of the organization
     * @param debtPositionTypeOrgId the identifier of the specific debt position type to verify for authorization
     * @param username the username of the operator
     * @return the {@link DebtPositionTypeOrgDTO} representing the authorized installment type
     * @throws OperatorNotAuthorizedException if the operator is not authorized to manage the specified installment type
     * @see AuthorizeOperatorOnDebtPositionTypeActivity
     */

    public DebtPositionTypeOrgDTO authorize(Long orgId, Long debtPositionTypeOrgId, String username){
        Optional<DebtPositionTypeOrgDTO> debtPositionTypeOrg =
                deptPositionTypeOrgDao.getAuthorizedDebtPositionTypeOrgs(orgId, debtPositionTypeOrgId, username);

        return debtPositionTypeOrg
                .orElseThrow(() -> new OperatorNotAuthorizedException("The operator " + username + " is not authorized on the DebtPositionTypeOrg " + orgId));
    }
}
