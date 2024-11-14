package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.dao.OrganizationInstallmentTypeDao;
import it.gov.pagopa.payhub.activities.dto.InstallmentOperatorDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationInstallmentTypeDTO;
import it.gov.pagopa.payhub.activities.exception.custom.ValidatorException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerifyAuthorizationInstallmentsActivity {

    private final OrganizationInstallmentTypeDao organizationInstallmentTypeDao;

    public VerifyAuthorizationInstallmentsActivity(OrganizationInstallmentTypeDao organizationInstallmentTypeDao) {
        this.organizationInstallmentTypeDao = organizationInstallmentTypeDao;
    }

    public OrganizationInstallmentTypeDTO verifyAuth(InstallmentOperatorDTO installmentsOperatorDTO, String username, Long mygovEnteId){

        List<OrganizationInstallmentTypeDTO> organizationInstallmentTypeList =
                organizationInstallmentTypeDao.getByMygovEnteIdAndOperatoreUsername(mygovEnteId, username);

        return organizationInstallmentTypeList.stream()
                .filter(etd -> etd.getTypeCode().equals(installmentsOperatorDTO.getOrganizationTypeInstallment().getTypeCode()))
                .findFirst()
                .orElseThrow(() -> new ValidatorException("OrganizationInstallmentType is not active for this operator"));
    }
}
