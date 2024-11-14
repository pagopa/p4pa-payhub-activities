package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.dao.OrganizationInstallmentTypeDao;
import it.gov.pagopa.payhub.activities.dto.InstallmentsOperatorDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationInstallmentTypeDTO;
import it.gov.pagopa.payhub.activities.exception.custom.ValidatorException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerifyAuthorizationInstallments {

    private final OrganizationInstallmentTypeDao organizationInstallmentTypeDao;

    public VerifyAuthorizationInstallments(OrganizationInstallmentTypeDao organizationInstallmentTypeDao) {
        this.organizationInstallmentTypeDao = organizationInstallmentTypeDao;
    }

    public OrganizationInstallmentTypeDTO verifyAuth(InstallmentsOperatorDTO installmentsOperatorDTO, String username, Long mygovEnteId){

        List<OrganizationInstallmentTypeDTO> organizationInstallmentTypeList =
                organizationInstallmentTypeDao.getByMygovEnteIdAndOperatoreUsername(mygovEnteId, username);

        return organizationInstallmentTypeList.stream()
                .filter(etd -> etd.getTypeCode().equals(installmentsOperatorDTO.getOrganizationTypeInstallment().getTypeCode()))
                .findFirst()
                .orElseThrow(() -> new ValidatorException("OrganizationInstallmentType is not active for this operator"));
    }
}
