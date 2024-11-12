package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.dao.OrganizationInstallmentTypeDao;
import it.gov.pagopa.payhub.activities.dto.InstallmentsOperatorDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationInstallmentTypeDTO;
import it.gov.pagopa.payhub.activities.exception.ValidatorException;

import java.util.List;

public class VerifyAuthorizationInstallments {

    private final OrganizationInstallmentTypeDao organizationInstallmentTypeDao;

    public VerifyAuthorizationInstallments(OrganizationInstallmentTypeDao organizationInstallmentTypeDao) {
        this.organizationInstallmentTypeDao = organizationInstallmentTypeDao;
    }

    public OrganizationInstallmentTypeDTO verifyAuth(InstallmentsOperatorDTO installmentsOperatorDTO, String username, Long mygovEnteId){

        List<OrganizationInstallmentTypeDTO> organizationInstallmentTypeList =
                organizationInstallmentTypeDao.getByMygovEnteIdAndOperatoreUsername(mygovEnteId, username);

        return organizationInstallmentTypeList.stream()
                .filter(etd -> etd.getCodTipo().equals(installmentsOperatorDTO.getTipoDovuto().getCodTipo()))
                .findFirst()
                .orElseThrow(() -> new ValidatorException("OrganizationInstallmentType is not active for this operator"));
    }
}
