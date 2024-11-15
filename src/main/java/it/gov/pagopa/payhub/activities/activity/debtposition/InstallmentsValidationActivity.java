package it.gov.pagopa.payhub.activities.activity.debtposition;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.payhub.activities.dao.PositionDao;
import it.gov.pagopa.payhub.activities.dao.FlowDao;
import it.gov.pagopa.payhub.activities.dto.*;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;
import it.gov.pagopa.payhub.activities.dto.position.CityDTO;
import it.gov.pagopa.payhub.activities.dto.position.NationDTO;
import it.gov.pagopa.payhub.activities.dto.position.ProvinceDTO;
import it.gov.pagopa.payhub.activities.exception.custom.FlowException;
import it.gov.pagopa.payhub.activities.exception.custom.ValidatorException;
import it.gov.pagopa.payhub.activities.utility.Utilities;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for validate the installment values and its debt position type.
 * This class provides methods to validate flow of organization to associate to installment, mandatory fields,
 * position data or formality of values.
 */
@Service
public class InstallmentsValidationActivity {

    private final FlowDao flowDao;
    private final PositionDao positionDao;

    public InstallmentsValidationActivity(FlowDao flowDao, PositionDao positionDao) {
        this.flowDao = flowDao;
        this.positionDao = positionDao;
    }

    /**
     * Verifies if exists a flow associated with organization
     * @param organizationId the identifier of the organization
     * @param isSpontaneous if the flow has the spontaneous flag
     * @return the first {@link FlowDTO} found for organization requested
     * @throws FlowException if the flow does not exist
     */
    public FlowDTO validateFlow(Long organizationId, boolean isSpontaneous) {
        List<FlowDTO> flows = flowDao.getFlowsByOrganization(organizationId, isSpontaneous);

        if (flows == null || flows.isEmpty())
            throw new FlowException("No flow was found for organization with id " + organizationId);

        return flows.get(0);
    }

    /**
     * Validates that the mandatory installment fields are valued and that some fields are formally correct
     * @param installmentDTO the {@link InstallmentDTO}
     * @param debtPositionTypeOrgDTO the {@link DebtPositionTypeOrgDTO} representing the installment type
     * @throws ValidatorException if one field is invalid
     */
    public void formalValidation(InstallmentDTO installmentDTO, DebtPositionTypeOrgDTO debtPositionTypeOrgDTO) {
        if (installmentDTO.getInstallmentDebtPositionTypeOrg() == null ||
                StringUtils.isBlank(installmentDTO.getInstallmentDebtPositionTypeOrg().getTypeCode())) {
            throw new ValidatorException("Organization installment type is mandatory");
        }

        if (StringUtils.isBlank(installmentDTO.getBeneficiaryName())) {
            throw new ValidatorException("Beneficiary name is mandatory");
        }

        if (StringUtils.isNotBlank(installmentDTO.getEmail()) && !Utilities.isValidEmail(installmentDTO.getEmail())) {
            throw new ValidatorException("Email is not valid");
        }

        if (StringUtils.isBlank(installmentDTO.getRemittanceInformation())) {
            throw new ValidatorException("Remittance information is mandatory");
        }

        if (installmentDTO.getDueDate() != null && installmentDTO.getDueDate().isBefore(LocalDate.now())) {
            throw new ValidatorException("The due date cannot be retroactive");
        }

        if (debtPositionTypeOrgDTO.isFlagMandatoryDueDate() && installmentDTO.getDueDate() == null) {
            throw new ValidatorException("The due date is mandatory");
        }
    }

    /**
     * Validates that the unique identification code of installment is correctly valued
     * @param installmentDTO the {@link InstallmentDTO}
     * @param debtPositionTypeOrgDTO the {@link DebtPositionTypeOrgDTO} representing the installment type
     * @return the uniqueIdentificationCode validated
     * @throws ValidatorException if the field is invalid
     */
    public String validateUniqueIdentificationCode(InstallmentDTO installmentDTO, DebtPositionTypeOrgDTO debtPositionTypeOrgDTO){
        String uniqueIdentificationCode = installmentDTO.getUniqueIdentificationCode();

        if (debtPositionTypeOrgDTO.isFlagAnonymousFiscalCode()) {
            if(installmentDTO.isFlagAnonymousData()){
                uniqueIdentificationCode = "ANONIMO";
            } else if (StringUtils.isBlank(installmentDTO.getUniqueIdentificationCode())){
                throw new ValidatorException("This organization installment type or installment does not allow an anonymous unique identification code");
            }
        } else {
            if(StringUtils.isBlank(installmentDTO.getUniqueIdentificationCode())){
                throw new ValidatorException("Unique identification code is mandatory");
            }
        }
        return uniqueIdentificationCode;
    }

    /**
     * Validates that the amount of installmentDTO is correctly valued
     * @param installmentDTO the {@link InstallmentDTO}
     * @param debtPositionTypeOrgDTO the {@link DebtPositionTypeOrgDTO} representing the installment type
     * @throws ValidatorException if the field is invalid
     */
    public void validateAmountInstallment(InstallmentDTO installmentDTO, DebtPositionTypeOrgDTO debtPositionTypeOrgDTO){
        if (StringUtils.isBlank(installmentDTO.getAmount())) {
            throw new ValidatorException("Amount is mandatory");
        }

        try {
            BigDecimal amountInstallment = new BigDecimal(installmentDTO.getAmount()).setScale(2, RoundingMode.HALF_EVEN);

            if (debtPositionTypeOrgDTO.getAmount() != null) {
                if (!amountInstallment.equals(debtPositionTypeOrgDTO.getAmount().setScale(2, RoundingMode.HALF_EVEN)))
                    throw new ValidatorException("Invalid amount for this installment type");
            } else {
                if (amountInstallment.compareTo(BigDecimal.ZERO) < (installmentDTO.isFlagMultiBeneficiary() ? 0 : 1)) {
                    throw new ValidatorException("Invalid amount");
                }
            }
        } catch (ValidatorException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidatorException("Invalid amount");
        }
    }

    /**
     * Validates that nation and postal code of installment debtor are correctly valued
     * @param installmentDTO the {@link InstallmentDTO}
     * @return the {@link NationDTO} obtained from isoCode if nation is valued in installment
     * @throws ValidatorException if one field is invalid
     */
    public NationDTO validateNationAndPostalCode(InstallmentDTO installmentDTO) {
        if (StringUtils.isNotBlank(installmentDTO.getPostalCode())) {
            if (installmentDTO.getNation() == null || StringUtils.isBlank(installmentDTO.getNation().getCodeIsoAlpha2())) {
                throw new ValidatorException("Nation is not valid");
            } else {
                if (!Utilities.isValidPostalCode(installmentDTO.getPostalCode(), installmentDTO.getNation().getCodeIsoAlpha2())) {
                    throw new ValidatorException("Postal code is not valid");
                }
            }
        }

        NationDTO nation = null;
        if (installmentDTO.getNation() != null && StringUtils.isNotBlank(installmentDTO.getNation().getCodeIsoAlpha2())) {
            nation = Optional.ofNullable(positionDao.getNationByCodeIso(installmentDTO.getNation().getCodeIsoAlpha2()))
                    .orElseThrow(() -> new ValidatorException("Nation is not valid"));
        }
        return nation;
    }

    /**
     * Validates that the province of the installment debtor is correctly valued
     * @param installmentDTO the {@link InstallmentDTO}
     * @param nationDTO the {@link NationDTO} about installment debtor
     * @return the {@link ProvinceDTO} obtained from acronym
     * @throws ValidatorException if one field is invalid
     */
    public ProvinceDTO validateProvince(InstallmentDTO installmentDTO, NationDTO nationDTO){
        ProvinceDTO province = null;
        if (installmentDTO.getProvince() != null && StringUtils.isNotBlank(installmentDTO.getProvince().getAcronym())) {
            if (nationDTO == null || !nationDTO.hasProvince())
                throw new ValidatorException("Province is not valid");
            province = Optional.ofNullable(positionDao.getProvinceByAcronym(installmentDTO.getProvince().getAcronym()))
                    .orElseThrow(() -> new ValidatorException("Province is not valid"));
        }
        return province;
    }

    /**
     * Validates that the municipality of installmentDTO debtor is correctly valued
     * @param installmentDTO the {@link InstallmentDTO}
     * @param provinceDTO the {@link ProvinceDTO} about installment debtor
     * @return the name of the municipality after validation
     * @throws ValidatorException if one field is invalid
     */
    public String validateMunicipality(InstallmentDTO installmentDTO, ProvinceDTO provinceDTO){
        String municipality = null;
        if (installmentDTO.getMunicipality() != null &&
                StringUtils.isNotBlank(installmentDTO.getMunicipality().getMunicipality())) {
            if (provinceDTO == null) {
                throw new ValidatorException("Location is not valid");
            }
            municipality = positionDao.getMunicipalityByNameAndProvince(installmentDTO.getMunicipality().getMunicipality(), provinceDTO.getAcronym())
                    .map(CityDTO::getMunicipality).orElse(installmentDTO.getMunicipality().getMunicipality());
        }
        return municipality;
    }

    /**
     * Validates that the address and civic of installmentDTO debtor are correctly valued
     * @param installmentDTO the {@link InstallmentDTO}
     */
    public void validateAddress(InstallmentDTO installmentDTO) {
        if (StringUtils.isNotBlank(installmentDTO.getAddress()) &&
                !Utilities.validateAddress(installmentDTO.getAddress(), false)) {
            throw new ValidatorException("Address is not valid");
        }

        if (StringUtils.isNotBlank(installmentDTO.getCivic()) &&
                !Utilities.validateCivic(installmentDTO.getCivic(), false)) {
            throw new ValidatorException("Civic is not valid");
        }
    }

}
