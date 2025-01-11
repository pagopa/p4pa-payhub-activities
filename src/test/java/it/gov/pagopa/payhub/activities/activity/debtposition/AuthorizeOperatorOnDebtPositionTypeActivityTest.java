package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.DebtPositionTypeOrgDao;
import it.gov.pagopa.payhub.activities.exception.OperatorNotAuthorizedException;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizeOperatorOnDebtPositionTypeActivityTest {

    @Mock
    private DebtPositionTypeOrgDao debtPositionTypeOrgDao;

    private AuthorizeOperatorOnDebtPositionTypeActivity authorizeOperatorOnDebtPositionTypeActivity;

    @BeforeEach
    void init() {
        authorizeOperatorOnDebtPositionTypeActivity = new AuthorizeOperatorOnDebtPositionTypeActivityImpl(debtPositionTypeOrgDao);
    }

    @Test
    void givenAuthorizeThenSuccess() {
        String username = "username";
        Long orgId = 1L;
        Long debtPositionTypeOrgId = 1L;

        DebtPositionTypeOrg debtPositionTypeOrgDTO = new DebtPositionTypeOrg();
        debtPositionTypeOrgDTO.setDebtPositionTypeOrgId(debtPositionTypeOrgId);
        debtPositionTypeOrgDTO.setOrganizationId(orgId);

        when(debtPositionTypeOrgDao.getAuthorizedDebtPositionTypeOrg(orgId, debtPositionTypeOrgId, username))
                .thenReturn(Optional.of(debtPositionTypeOrgDTO));

        DebtPositionTypeOrg result = authorizeOperatorOnDebtPositionTypeActivity.authorize(orgId, debtPositionTypeOrgId, username);

        assertEquals(debtPositionTypeOrgDTO, result);
    }

    @Test
    void givenAuthorizeWhenNotEqualTypeCodeThenThrowValidatorException() {
        String username = "username";
        Long orgId = 1L;
        Long debtPositionTypeOrgId = 1L;

        DebtPositionTypeOrg debtPositionTypeOrgDTO = new DebtPositionTypeOrg();
        debtPositionTypeOrgDTO.setDebtPositionTypeOrgId(1L);

        when(debtPositionTypeOrgDao.getAuthorizedDebtPositionTypeOrg(orgId, debtPositionTypeOrgId, username))
                .thenReturn(Optional.empty());

        assertThrows(OperatorNotAuthorizedException.class, () ->
                authorizeOperatorOnDebtPositionTypeActivity.authorize(orgId, debtPositionTypeOrgId, username));
    }
}

