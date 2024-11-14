package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.dao.OrganizationInstallmentTypeDao;
import it.gov.pagopa.payhub.activities.dto.InstallmentsOperatorDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationInstallmentTypeDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationTypeInstallmentDTO;
import it.gov.pagopa.payhub.activities.exception.custom.ValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyAuthorizationInstallmentsActivityTest {

    @Mock
    private OrganizationInstallmentTypeDao organizationInstallmentTypeDao;

    private VerifyAuthorizationInstallmentsActivity verifyAuthorizationInstallmentsActivity;

    @BeforeEach
    void init() {
        verifyAuthorizationInstallmentsActivity = new VerifyAuthorizationInstallmentsActivity(organizationInstallmentTypeDao);
    }

    @Test
    void givenVerifyAuthThenSuccess() {
        String username = "username";
        Long mygovEnteId = 1L;
        InstallmentsOperatorDTO installmentsOperatorDTO = new InstallmentsOperatorDTO();
        OrganizationTypeInstallmentDTO organizationTypeInstallmentDTO = new OrganizationTypeInstallmentDTO();

        installmentsOperatorDTO.setOrganizationTypeInstallment(organizationTypeInstallmentDTO);
        organizationTypeInstallmentDTO.setTypeCode("TYPE_CODE");

        OrganizationInstallmentTypeDTO organizationInstallmentTypeDTO = new OrganizationInstallmentTypeDTO();
        organizationInstallmentTypeDTO.setTypeCode("TYPE_CODE");

        when(organizationInstallmentTypeDao.getByMygovEnteIdAndOperatoreUsername(mygovEnteId, username))
                .thenReturn(List.of(organizationInstallmentTypeDTO));

        OrganizationInstallmentTypeDTO result = verifyAuthorizationInstallmentsActivity.verifyAuth(installmentsOperatorDTO, username, mygovEnteId);

        assertEquals(organizationInstallmentTypeDTO, result);
    }

    @Test
    void givenVerifyAuthWhenNotEqualTypeCodeThenThrowValidatorException() {
        String username = "username";
        Long mygovEnteId = 1L;
        InstallmentsOperatorDTO installmentsOperatorDTO = new InstallmentsOperatorDTO();

        OrganizationInstallmentTypeDTO organizationInstallmentTypeDTO = new OrganizationInstallmentTypeDTO();
        organizationInstallmentTypeDTO.setTypeCode("TYPE_CODE");

        when(organizationInstallmentTypeDao.getByMygovEnteIdAndOperatoreUsername(mygovEnteId, username))
                .thenReturn(List.of());

        assertThrows(ValidatorException.class, () ->
                verifyAuthorizationInstallmentsActivity.verifyAuth(installmentsOperatorDTO, username, mygovEnteId));
    }
}

