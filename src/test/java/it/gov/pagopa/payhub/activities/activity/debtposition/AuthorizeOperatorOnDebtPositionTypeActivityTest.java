package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.DebtPositionTypeOrgDao;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.exception.OperatorNotAuthorizedException;
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

        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setOrgId(orgId);
        debtPositionTypeOrgDTO.setDebtPositionTypeOrgId(debtPositionTypeOrgId);
        debtPositionTypeOrgDTO.setOrg(organizationDTO);

        when(debtPositionTypeOrgDao.getAuthorizedDebtPositionTypeOrg(orgId, debtPositionTypeOrgId, username))
                .thenReturn(Optional.of(debtPositionTypeOrgDTO));

        DebtPositionTypeOrgDTO result = authorizeOperatorOnDebtPositionTypeActivity.authorize(orgId, debtPositionTypeOrgId, username);

        assertEquals(debtPositionTypeOrgDTO, result);
    }

    @Test
    void givenAuthorizeWhenNotEqualTypeCodeThenThrowValidatorException() {
        String username = "username";
        Long orgId = 1L;
        Long debtPositionTypeOrgId = 1L;

        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        debtPositionTypeOrgDTO.setDebtPositionTypeOrgId(1L);

        when(debtPositionTypeOrgDao.getAuthorizedDebtPositionTypeOrg(orgId, debtPositionTypeOrgId, username))
                .thenReturn(Optional.empty());

        assertThrows(OperatorNotAuthorizedException.class, () ->
                authorizeOperatorOnDebtPositionTypeActivity.authorize(orgId, debtPositionTypeOrgId, username));
    }
}

