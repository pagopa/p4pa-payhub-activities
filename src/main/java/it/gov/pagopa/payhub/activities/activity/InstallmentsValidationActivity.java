package it.gov.pagopa.payhub.activities.activity;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.payhub.activities.dao.LocationDao;
import it.gov.pagopa.payhub.activities.dao.FlowDao;
import it.gov.pagopa.payhub.activities.dto.*;
import it.gov.pagopa.payhub.activities.exception.FlowException;
import it.gov.pagopa.payhub.activities.exception.ValidatorException;
import it.gov.pagopa.payhub.activities.utility.Utilities;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InstallmentsValidationActivity {

    private final FlowDao flowDao;
    private final LocationDao locationDao;

    public InstallmentsValidationActivity(FlowDao flowDao, LocationDao locationDao) {
        this.flowDao = flowDao;
        this.locationDao = locationDao;
    }

    public FlowDTO validateFlow(Long organizationId, boolean isSpontaneous) {
        List<FlowDTO> flows = flowDao.getFlowsByOrganization(organizationId, isSpontaneous);

        if (flows == null || flows.isEmpty())
            throw new FlowException("No flow was found for organization with id " + organizationId);

        return flows.get(0);
    }

    public void formalValidation(InstallmentOperatorDTO installment, OrganizationInstallmentTypeDTO orgInstallmentType) {
        if (installment.getOrganizationTypeInstallment() == null ||
                StringUtils.isBlank(installment.getOrganizationTypeInstallment().getTypeCode())) {
            throw new ValidatorException("Organization installment type is mandatory");
        }

        if (StringUtils.isBlank(installment.getBeneficiaryName())) {
            throw new ValidatorException("Beneficiary name is mandatory");
        }

        if (StringUtils.isNotBlank(installment.getEmail()) && !Utilities.isValidEmail(installment.getEmail())) {
            throw new ValidatorException("Email is not valid");
        }

        if (StringUtils.isBlank(installment.getRemittanceInformation())) {
            throw new ValidatorException("Remittance information is mandatory");
        }

        if (installment.getDueDate() != null && installment.getDueDate().isBefore(LocalDate.now())) {
            throw new ValidatorException("The due date cannot be retroactive");
        }

        if (orgInstallmentType.isFlagMandatoryDueDate() && installment.getDueDate() == null) {
            throw new ValidatorException("The due date is mandatory");
        }
    }

    public String validateUniqueIdentificationCode(InstallmentOperatorDTO installment, OrganizationInstallmentTypeDTO orgInstallmentType){
        if(!orgInstallmentType.isFlagAnonymousFiscalCode() && StringUtils.isBlank(installment.getUniqueIdentificationCode())){
            throw new ValidatorException("Unique identification code is mandatory");
        }
        String uniqueIdentificationCode = null;

        if (orgInstallmentType.isFlagAnonymousFiscalCode()) {
            if(installment.isFlagAnonymousData()){
                uniqueIdentificationCode = "ANONIMO";
            } else if (StringUtils.isNotBlank(installment.getUniqueIdentificationCode())){
                uniqueIdentificationCode = installment.getUniqueIdentificationCode();
            } else {
                throw new ValidatorException("This organization installment type or installment does not allow an anonymous unique identification code");
            }
        }
        return uniqueIdentificationCode;
    }

    public void validateAmountInstallment(InstallmentOperatorDTO installment, OrganizationInstallmentTypeDTO orgInstallmentType){
        if (StringUtils.isBlank(installment.getAmount())) {
            throw new ValidatorException("Amount is mandatory");
        }

        try {
            BigDecimal amountInstallment = new BigDecimal(installment.getAmount()).setScale(2, RoundingMode.HALF_EVEN);

            if (orgInstallmentType.getAmount() != null) {
                if (!amountInstallment.equals(orgInstallmentType.getAmount()))
                    throw new ValidatorException("Invalid amount for this installment type");
            } else {
                if (amountInstallment.compareTo(BigDecimal.ZERO) < (installment.isFlagMultiBeneficiary() ? 0 : 1)) {
                    throw new ValidatorException("Invalid amount");
                }
            }
        } catch (ValidatorException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidatorException("Invalid amount");
        }
    }

    public NationDTO validateNationAndPostalCode(InstallmentOperatorDTO installment) {
        if (StringUtils.isNotBlank(installment.getPostalCode())) {
            if (installment.getNation() == null || StringUtils.isBlank(installment.getNation().getCodeIsoAlpha2())) {
                throw new ValidatorException("Nation is not valid");
            } else {
                if (!Utilities.isValidPostalCode(installment.getPostalCode(), installment.getNation().getCodeIsoAlpha2())) {
                    throw new ValidatorException("Postal code is not valid");
                }
            }
        }

        NationDTO nation = null;
        if (installment.getNation() != null && StringUtils.isNotBlank(installment.getNation().getCodeIsoAlpha2())) {
            nation = Optional.ofNullable(locationDao.getNationByCodeIso(installment.getNation().getCodeIsoAlpha2()))
                    .orElseThrow(() -> new ValidatorException("Nation is not valid"));
        }
        return nation;
    }

    public ProvinceDTO validateProvince(InstallmentOperatorDTO installment, NationDTO nation){
        ProvinceDTO province = null;
        if (installment.getProvince() != null && StringUtils.isNotBlank(installment.getProvince().getAcronym())) {
            if (nation == null || !nation.hasProvince())
                throw new ValidatorException("Province is not valid");
            province = Optional.ofNullable(locationDao.getProvinceByAcronym(installment.getProvince().getAcronym()))
                    .orElseThrow(() -> new ValidatorException("Province is not valid"));
        }
        return province;
    }

    public String validateMunicipality(InstallmentOperatorDTO installment, ProvinceDTO province){
        String municipality = null;
        if (installment.getMunicipality() != null &&
                StringUtils.isNotBlank(installment.getMunicipality().getMunicipality())) {
            if (province == null) {
                throw new ValidatorException("Location is not valid");
            }
            municipality = locationDao.getMunicipalityByNameAndProvince(installment.getMunicipality().getMunicipality(), province.getAcronym())
                    .map(CityDTO::getMunicipality).orElse(installment.getMunicipality().getMunicipality());
        }
        return municipality;
    }
    public void validateAddress(InstallmentOperatorDTO installment) {
        if (StringUtils.isNotBlank(installment.getAddress()) &&
                !Utilities.validateAddress(installment.getAddress(), false)) {
            throw new ValidatorException("Address is not valid");
        }

        if (StringUtils.isNotBlank(installment.getCivic()) &&
                !Utilities.validateCivic(installment.getCivic(), false)) {
            throw new ValidatorException("Civic is not valid");
        }
    }

}
