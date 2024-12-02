package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.AddressDao;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.PersonDTO;
import it.gov.pagopa.payhub.activities.dto.PersonRequestDTO;
import it.gov.pagopa.payhub.activities.dto.address.CityDTO;
import it.gov.pagopa.payhub.activities.dto.address.NationDTO;
import it.gov.pagopa.payhub.activities.dto.address.ProvinceDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.*;
import it.gov.pagopa.payhub.activities.exception.ValidationException;
import it.gov.pagopa.payhub.activities.utility.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Lazy
@Service
public class ValidateDebtPositionActivityImpl implements ValidateDebtPositionActivity {

    private final IngestionFlowFileDao ingestionFlowFileDao;
    private final AddressDao addressDao;

    public ValidateDebtPositionActivityImpl(IngestionFlowFileDao ingestionFlowFileDao, AddressDao addressDao) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.addressDao = addressDao;
    }

    public DebtPositionDTO validate(DebtPositionRequestDTO debtPositionRequestDTO) {
        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();

        List<IngestionFlowFileDTO> flows = ingestionFlowFileDao.getIngestionFlowFilesByOrganization(debtPositionRequestDTO.getOrg().getOrgId(), true);

        if (flows == null || flows.isEmpty())
            throw new ValidationException("No flow was found for organization with id " + debtPositionRequestDTO.getOrg().getOrgId());

        debtPositionDTO.setIngestionFlowFile(flows.get(0));

        if (debtPositionRequestDTO.getDebtPositionTypeOrg() == null ||
                StringUtils.isBlank(debtPositionRequestDTO.getDebtPositionTypeOrg().getDebtPositionType().getCode())) {
            throw new ValidationException("Debt position type organization is mandatory");
        }
        debtPositionDTO.setDebtPositionTypeOrg(debtPositionRequestDTO.getDebtPositionTypeOrg());

        List<PaymentOptionDTO> paymentOptionsDTO = new ArrayList<>();

        for (PaymentOptionRequestDTO paymentOptionRequestDTO : debtPositionRequestDTO.getPaymentOptions()) {
            PaymentOptionDTO paymentOptionDTO = new PaymentOptionDTO();
            List<InstallmentDTO> installmentsDTO = new ArrayList<>();
            for (InstallmentRequestDTO installmentRequestDTO : paymentOptionRequestDTO.getInstallments()) {
                InstallmentDTO installmentDTO = new InstallmentDTO();
                validateInstallment(installmentRequestDTO,
                        debtPositionRequestDTO.getDebtPositionTypeOrg(),
                        installmentDTO);

                PersonDTO personDTO = new PersonDTO();
                validatePersonData(installmentRequestDTO.getDebtor(),
                        debtPositionRequestDTO.getDebtPositionTypeOrg(),
                        personDTO);
                installmentDTO.setPayer(personDTO);

                installmentsDTO.add(installmentDTO);
            }
            paymentOptionDTO.setInstallments(installmentsDTO);
            paymentOptionsDTO.add(paymentOptionDTO);
        }

        debtPositionDTO.setPaymentOptions(paymentOptionsDTO);

        return debtPositionDTO;
    }


    private void validateInstallment(InstallmentRequestDTO installmentRequestDTO, DebtPositionTypeOrgDTO debtPositionTypeOrgDTO,
                                     InstallmentDTO installmentDTO) {
        if (StringUtils.isBlank(installmentRequestDTO.getRemittanceInformation())) {
            throw new ValidationException("Remittance information is mandatory");
        }
        installmentDTO.setRemittanceInformation(installmentRequestDTO.getRemittanceInformation());

        if (installmentRequestDTO.getDueDate() != null && installmentRequestDTO.getDueDate().isBefore(LocalDate.now())) {
            throw new ValidationException("The due date cannot be retroactive");
        }

        if (debtPositionTypeOrgDTO.isFlagMandatoryDueDate() && installmentRequestDTO.getDueDate() == null) {
            throw new ValidationException("The due date is mandatory");
        }
        installmentDTO.setDueDate(installmentRequestDTO.getDueDate());

        if (installmentRequestDTO.getAmount() == null) {
            throw new ValidationException("Amount is mandatory");
        }
        if (installmentRequestDTO.getAmount() < 0) {
            throw new ValidationException("Invalid amount");
        }
        if (debtPositionTypeOrgDTO.getAmount() != null && !installmentRequestDTO.getAmount().equals(debtPositionTypeOrgDTO.getAmount())) {
            throw new ValidationException("Invalid amount for this debt position type org");
        }
        installmentDTO.setAmount(installmentRequestDTO.getAmount());
    }


    private void validatePersonData(PersonRequestDTO personRequestDTO, DebtPositionTypeOrgDTO debtPositionTypeOrgDTO,
                                    PersonDTO personDTO) {
        String uniqueIdentificationCode = personRequestDTO.getUniqueIdentifierCode();

        if (debtPositionTypeOrgDTO.isFlagAnonymousFiscalCode()) {
            if (personRequestDTO.isFlagAnonymousData()) {
                uniqueIdentificationCode = "ANONIMO";
            } else if (StringUtils.isBlank(personRequestDTO.getUniqueIdentifierCode())) {
                throw new ValidationException("This organization installment type or installment does not allow an anonymous unique identification code");
            }
        } else {
            if (StringUtils.isBlank(personRequestDTO.getUniqueIdentifierCode())) {
                throw new ValidationException("Unique identification code is mandatory");
            }
        }
        personDTO.setUniqueIdentifierCode(uniqueIdentificationCode);

        if (StringUtils.isBlank(personRequestDTO.getFullName())) {
            throw new ValidationException("Beneficiary name is mandatory");
        }
        personDTO.setFullName(personRequestDTO.getFullName());

        if (StringUtils.isNotBlank(personRequestDTO.getEmail()) &&
                !Utilities.isValidEmail(personRequestDTO.getEmail())) {
            throw new ValidationException("Email is not valid");
        }
        personDTO.setEmail(personRequestDTO.getEmail());

        NationDTO nationDTO = validateNation(personRequestDTO);
        personDTO.setNation(nationDTO != null ? nationDTO.getCodeIsoAlpha2() : null);

        ProvinceDTO provinceDTO = validateProvince(personRequestDTO, nationDTO);
        personDTO.setProvince(provinceDTO != null ? provinceDTO.getAcronym() : null);

        String municipality = validateMunicipality(personRequestDTO, provinceDTO);
        personDTO.setLocation(municipality);

        validateAddress(personRequestDTO);
        personDTO.setAddress(personRequestDTO.getAddress());
        personDTO.setCivic(personRequestDTO.getCivic());
    }

    private NationDTO validateNation(PersonRequestDTO personRequestDTO) {
        if (StringUtils.isNotBlank(personRequestDTO.getPostalCode())) {
            if (personRequestDTO.getNation() == null || StringUtils.isBlank(personRequestDTO.getNation().getCodeIsoAlpha2())) {
                throw new ValidationException("Nation is not valid");
            } else {
                if (!Utilities.isValidPostalCode(personRequestDTO.getPostalCode(), personRequestDTO.getNation().getCodeIsoAlpha2())) {
                    throw new ValidationException("Postal code is not valid");
                }
            }
        }

        NationDTO nation = null;
        if (personRequestDTO.getNation() != null && StringUtils.isNotBlank(personRequestDTO.getNation().getCodeIsoAlpha2())) {
            nation = Optional.ofNullable(addressDao.getNationByCodeIso(personRequestDTO.getNation().getCodeIsoAlpha2()))
                    .orElseThrow(() -> new ValidationException("Nation is not valid"));
        }
        return nation;
    }

    private ProvinceDTO validateProvince(PersonRequestDTO personRequestDTO, NationDTO nationDTO) {
        ProvinceDTO province = null;
        if (personRequestDTO.getProvince() != null && StringUtils.isNotBlank(personRequestDTO.getProvince().getAcronym())) {
            if (nationDTO == null || !nationDTO.hasProvince())
                throw new ValidationException("Province is not valid");
            province = Optional.ofNullable(addressDao.getProvinceByAcronym(personRequestDTO.getProvince().getAcronym()))
                    .orElseThrow(() -> new ValidationException("Province is not valid"));
        }
        return province;
    }

    private String validateMunicipality(PersonRequestDTO personRequestDTO, ProvinceDTO provinceDTO) {
        String municipality = null;
        if (personRequestDTO.getLocation() != null &&
                StringUtils.isNotBlank(personRequestDTO.getLocation().getMunicipality())) {
            if (provinceDTO == null) {
                throw new ValidationException("Location is not valid");
            }
            municipality = addressDao.getMunicipalityByNameAndProvince(personRequestDTO.getLocation().getMunicipality(), provinceDTO.getAcronym())
                    .map(CityDTO::getMunicipality).orElse(personRequestDTO.getLocation().getMunicipality());
        }
        return municipality;
    }

    private void validateAddress(PersonRequestDTO personRequestDTO) {
        if (StringUtils.isNotBlank(personRequestDTO.getAddress()) &&
                !Utilities.validateAddress(personRequestDTO.getAddress(), false)) {
            throw new ValidationException("Address is not valid");
        }

        if (StringUtils.isNotBlank(personRequestDTO.getCivic()) &&
                !Utilities.validateCivic(personRequestDTO.getCivic(), false)) {
            throw new ValidationException("Civic is not valid");
        }
    }
}
