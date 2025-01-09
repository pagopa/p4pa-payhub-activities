package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.TaxonomyDao;
import it.gov.pagopa.payhub.activities.dto.PersonDTO;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.PaymentOptionDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.payhub.activities.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.Utilities.isValidIban;
import static it.gov.pagopa.payhub.activities.util.Utilities.isValidPIVA;

@Lazy
@Service
public class ValidateDebtPositionActivityImpl implements ValidateDebtPositionActivity {

    private final TaxonomyDao taxonomyDao;

    public ValidateDebtPositionActivityImpl(TaxonomyDao taxonomyDao) {
        this.taxonomyDao = taxonomyDao;
    }

    public void validate(DebtPositionDTO debtPositionDTO) {
        if (debtPositionDTO.getDebtPositionTypeOrg() == null ||
                debtPositionDTO.getDebtPositionTypeOrg().getDebtPositionType() == null ||
                StringUtils.isBlank(debtPositionDTO.getDebtPositionTypeOrg().getDebtPositionType().getCode())) {
            throw new InvalidValueException("Debt position type organization is mandatory");
        }

        if (CollectionUtils.isEmpty(debtPositionDTO.getPaymentOptions())) {
            throw new InvalidValueException("Debt position payment options is mandatory");
        }

        for (PaymentOptionDTO paymentOptionDTO : debtPositionDTO.getPaymentOptions()) {
            if (CollectionUtils.isEmpty(paymentOptionDTO.getInstallments())) {
                throw new InvalidValueException("At least one installment of the debt position is mandatory");
            }
            for (InstallmentDTO installmentDTO : paymentOptionDTO.getInstallments()) {
                validateInstallment(installmentDTO, debtPositionDTO.getDebtPositionTypeOrg());
                validatePersonData(installmentDTO.getPayer(), debtPositionDTO.getDebtPositionTypeOrg());
                validateTransfers(installmentDTO.getTransfers());
            }
        }
    }

    private void validateInstallment(InstallmentDTO installmentDTO, DebtPositionTypeOrgDTO debtPositionTypeOrgDTO) {
        if (StringUtils.isBlank(installmentDTO.getRemittanceInformation())) {
            throw new InvalidValueException("Remittance information is mandatory");
        }
        if (installmentDTO.getDueDate() != null && installmentDTO.getDueDate().isBefore(LocalDate.now())) {
            throw new InvalidValueException("The due date cannot be retroactive");
        }
        if (debtPositionTypeOrgDTO.isFlagMandatoryDueDate() && installmentDTO.getDueDate() == null) {
            throw new InvalidValueException("The due date is mandatory");
        }
        if (installmentDTO.getAmount() == null) {
            throw new InvalidValueException("Amount is mandatory");
        }
        if (installmentDTO.getAmount() < 0) {
            throw new InvalidValueException("Amount is not valid");
        }
        if (debtPositionTypeOrgDTO.getAmount() != null && !installmentDTO.getAmount().equals(debtPositionTypeOrgDTO.getAmount())) {
            throw new InvalidValueException("Amount is not valid for this debt position type org");
        }
    }

    private void validatePersonData(PersonDTO personDTO, DebtPositionTypeOrgDTO debtPositionTypeOrgDTO) {
        if (personDTO == null) {
            throw new InvalidValueException("The debtor is mandatory for installment");
        }
        if (StringUtils.isBlank(personDTO.getUniqueIdentifierCode())) {
            throw new InvalidValueException("Unique identification code is mandatory");
        }
        if (!debtPositionTypeOrgDTO.isFlagAnonymousFiscalCode() && personDTO.getUniqueIdentifierCode().equals("ANONIMO")) {
            throw new InvalidValueException("This organization installment type or installment does not allow an anonymous unique identification code");
        }
        if (StringUtils.isBlank(personDTO.getFullName())) {
            throw new InvalidValueException("Beneficiary name is mandatory");
        }
        if (StringUtils.isBlank(personDTO.getEmail()) ||
                !Utilities.isValidEmail(personDTO.getEmail())) {
            throw new InvalidValueException("Email is not valid");
        }
    }

    private void validateTransfers(List<TransferDTO> transferDTOList) {
        if(CollectionUtils.isEmpty(transferDTOList)){
            throw new InvalidValueException("At least one transfer is mandatory for installment");
        }

        if (transferDTOList.size() > 1) {
            TransferDTO transferSecondaryBeneficiary = transferDTOList.stream()
                    .filter(transfer -> (transfer.getTransferIndex() == 2)).findAny()
                    .orElseThrow(() -> new InvalidValueException("Mismatch with transfers list"));

            if (StringUtils.isBlank(transferSecondaryBeneficiary.getOrgFiscalCode()) ||
                    !isValidPIVA(transferSecondaryBeneficiary.getOrgFiscalCode())) {
                throw new InvalidValueException("Fiscal code of secondary beneficiary is not valid");
            }
            if (!isValidIban(transferSecondaryBeneficiary.getIban())) {
                throw new InvalidValueException("Iban of secondary beneficiary is not valid");
            }
            checkTaxonomyCategory(transferSecondaryBeneficiary.getCategory());

            if (transferSecondaryBeneficiary.getAmount() == null || transferSecondaryBeneficiary.getAmount() < 0) {
                throw new InvalidValueException("The amount of secondary beneficiary is not valid");
            }
        }
    }

    private void checkTaxonomyCategory(String category) {
        if (StringUtils.isBlank(category)) {
            throw new InvalidValueException("Category of secondary beneficiary is mandatory");
        } else {
            String categoryCode = StringUtils.substringBeforeLast(category, "/") + "/";
            Boolean categoryCodeExists = taxonomyDao.verifyCategory(categoryCode);
            if (!Boolean.TRUE.equals(categoryCodeExists)) {
                throw new InvalidValueException("The category code does not exist in the archive");
            }
        }
    }
}
