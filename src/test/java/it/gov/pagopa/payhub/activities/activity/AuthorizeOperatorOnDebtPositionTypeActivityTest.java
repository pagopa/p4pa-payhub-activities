package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.debtposition.AuthorizeOperatorOnDebtPositionTypeActivity;
import it.gov.pagopa.payhub.activities.dao.DeptPositionTypeOrgDao;
import it.gov.pagopa.payhub.activities.dto.DebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationTypeInstallmentDTO;
import it.gov.pagopa.payhub.activities.exception.custom.OperatorNotAuthorizedException;
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
    private DeptPositionTypeOrgDao deptPositionTypeOrgDao;

    private AuthorizeOperatorOnDebtPositionTypeActivity authorizeOperatorOnDebtPositionTypeActivity;

    @BeforeEach
    void init() {
        authorizeOperatorOnDebtPositionTypeActivity = new AuthorizeOperatorOnDebtPositionTypeActivity(deptPositionTypeOrgDao);
    }

    @Test
    void givenAuthorizeThenSuccess() {
        String username = "username";
        Long orgId = 1L;
        Long debtPositionTypeOrgId = 1L;

        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setOrgId(orgId);
        debtPositionTypeOrgDTO.setDebtPositionTypeId(debtPositionTypeOrgId);
        debtPositionTypeOrgDTO.setOrgId(organizationDTO);

        when(deptPositionTypeOrgDao.getAuthorizedDebtPositionTypeOrgs(orgId, debtPositionTypeOrgId, username))
                .thenReturn(Optional.of(debtPositionTypeOrgDTO));

        DebtPositionTypeOrgDTO result = authorizeOperatorOnDebtPositionTypeActivity.authorize(orgId, debtPositionTypeOrgId, username);

        assertEquals(debtPositionTypeOrgDTO, result);
    }

    @Test
    void givenAuthorizeWhenNotEqualTypeCodeThenThrowValidatorException() {
        String username = "username";
        Long orgId = 1L;
        Long debtPositionTypeOrgId = 1L;

        OrganizationTypeInstallmentDTO organizationTypeInstallmentDTO = new OrganizationTypeInstallmentDTO();
        organizationTypeInstallmentDTO.setOrgId(orgId);


        DebtPositionTypeOrgDTO organizationInstallmentTypeDTO = new DebtPositionTypeOrgDTO();
        organizationInstallmentTypeDTO.setTypeCode("TYPE_CODE");

        when(deptPositionTypeOrgDao.getAuthorizedDebtPositionTypeOrgs(orgId, debtPositionTypeOrgId, username))
                .thenReturn(Optional.empty());

        assertThrows(OperatorNotAuthorizedException.class, () ->
                authorizeOperatorOnDebtPositionTypeActivity.authorize(orgId, debtPositionTypeOrgId, username));
    }
}

