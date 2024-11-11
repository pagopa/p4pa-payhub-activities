package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.constants.Constants;
import it.gov.pagopa.payhub.activities.dto.InstallmentsOperatorDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationInstallmentTypeDTO;
import it.gov.pagopa.payhub.activities.exception.ValidatorException;

import java.util.List;

public class VerifyAuthorizationInstallments {

    public OrganizationInstallmentTypeDTO verifyAuth(InstallmentsOperatorDTO installmentsOperatorDTO,
                                                     List<OrganizationInstallmentTypeDTO> organizationInstallmentTypeList){

        if (installmentsOperatorDTO.getTipoDovuto().getCodTipo().equals(Constants.TIPO_DOVUTO_MARCA_BOLLO_DIGITALE)){
            throw new ValidatorException("Operation not authorized for installment type MARCA_BOLLO_DIGITALE");
        }

        return organizationInstallmentTypeList.stream()
                .filter(etd -> etd.getCodTipo().equals(installmentsOperatorDTO.getTipoDovuto().getCodTipo()))
                .findFirst()
                .orElseThrow(() -> new ValidatorException("OrganizationInstallmentType is not active for this operator"));
    }
}
