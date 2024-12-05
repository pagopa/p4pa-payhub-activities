package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.TaxonomyDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.PersonDTO;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.PaymentOptionDTO;
import it.gov.pagopa.payhub.activities.exception.ValidationException;
import it.gov.pagopa.payhub.activities.utility.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

import static it.gov.pagopa.payhub.activities.utility.Utilities.isValidIban;
import static it.gov.pagopa.payhub.activities.utility.Utilities.isValidPIVA;

@Lazy
@Service
public class ValidateDebtPositionActivityImpl implements ValidateDebtPositionActivity {

    private final IngestionFlowFileDao ingestionFlowFileDao;
    private final TaxonomyDao taxonomyDao;

    public ValidateDebtPositionActivityImpl(IngestionFlowFileDao ingestionFlowFileDao, TaxonomyDao taxonomyDao) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.taxonomyDao = taxonomyDao;
    }

    public void validate(DebtPositionDTO debtPositionDTO) {
        List<IngestionFlowFileDTO> flows = ingestionFlowFileDao.getIngestionFlowFilesByOrganization(
                debtPositionDTO.getOrg().getOrgId(), true);

        if (CollectionUtils.isEmpty(flows)) {
            throw new ValidationException("No flow was found for organization with id " + debtPositionDTO.getOrg().getOrgId());
        }

        debtPositionDTO.setIngestionFlowFile(flows.get(0));

        if (debtPositionDTO.getDebtPositionTypeOrg() == null ||
                debtPositionDTO.getDebtPositionTypeOrg().getDebtPositionType() == null ||
                StringUtils.isBlank(debtPositionDTO.getDebtPositionTypeOrg().getDebtPositionType().getCode())) {
            throw new ValidationException("Debt position type organization is mandatory");
        }

        if (CollectionUtils.isEmpty(debtPositionDTO.getPaymentOptions())) {
            throw new ValidationException("Debt position payment options is mandatory");
        }

        for (PaymentOptionDTO paymentOptionDTO : debtPositionDTO.getPaymentOptions()) {
            if (CollectionUtils.isEmpty(paymentOptionDTO.getInstallments())) {
                throw new ValidationException("At least one installment of the debt position is mandatory");
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
            throw new ValidationException("Remittance information is mandatory");
        }
        if (installmentDTO.getDueDate() != null && installmentDTO.getDueDate().isBefore(LocalDate.now())) {
            throw new ValidationException("The due date cannot be retroactive");
        }
        if (debtPositionTypeOrgDTO.isFlagMandatoryDueDate() && installmentDTO.getDueDate() == null) {
            throw new ValidationException("The due date is mandatory");
        }
        if (installmentDTO.getAmount() == null) {
            throw new ValidationException("Amount is mandatory");
        }
        if (installmentDTO.getAmount() < 0) {
            throw new ValidationException("Amount is not valid");
        }
        if (debtPositionTypeOrgDTO.getAmount() != null && !installmentDTO.getAmount().equals(debtPositionTypeOrgDTO.getAmount())) {
            throw new ValidationException("Amount is not valid for this debt position type org");
        }
    }

    private void validatePersonData(PersonDTO personDTO, DebtPositionTypeOrgDTO debtPositionTypeOrgDTO) {
        if (personDTO == null) {
            throw new ValidationException("The debtor is mandatory for installment");
        }

        String uniqueIdentificationCode = personDTO.getUniqueIdentifierCode();

        if (debtPositionTypeOrgDTO.isFlagAnonymousFiscalCode()) {
            if (personDTO.isFlagAnonymousIdentifierCode()) {
                uniqueIdentificationCode = "ANONIMO";
            } else if (StringUtils.isBlank(personDTO.getUniqueIdentifierCode())) {
                throw new ValidationException("This organization installment type or installment does not allow an anonymous unique identification code");
            }
        } else {
            if (StringUtils.isBlank(personDTO.getUniqueIdentifierCode())) {
                throw new ValidationException("Unique identification code is mandatory");
            }
        }
        personDTO.setUniqueIdentifierCode(uniqueIdentificationCode);

        if (StringUtils.isBlank(personDTO.getFullName())) {
            throw new ValidationException("Beneficiary name is mandatory");
        }

        if (StringUtils.isBlank(personDTO.getEmail()) ||
                !Utilities.isValidEmail(personDTO.getEmail())) {
            throw new ValidationException("Email is not valid");
        }
    }

    private void validateTransfers(List<TransferDTO> transferDTOList) {
        if (transferDTOList != null && transferDTOList.size() > 1) {
            TransferDTO transferSecondaryBeneficiary = transferDTOList.stream()
                    .filter(transfer -> (transfer.getTransferIndex() == 2)).findAny()
                    .orElseThrow(() -> new ValidationException("Mismatch with transfers list"));

            if (StringUtils.isBlank(transferSecondaryBeneficiary.getOrgFiscalCode()) ||
                    !isValidPIVA(transferSecondaryBeneficiary.getOrgFiscalCode())) {
                throw new ValidationException("Fiscal code of secondary beneficiary is not valid");
            }
            if (!isValidIban(transferSecondaryBeneficiary.getIban())) {
                throw new ValidationException("Iban of secondary beneficiary is not valid");
            }
            checkTaxonomyCategory(transferSecondaryBeneficiary.getCategory());

            if (transferSecondaryBeneficiary.getAmount() == null || transferSecondaryBeneficiary.getAmount() < 0) {
                throw new ValidationException("The amount of secondary beneficiary is not valid");
            }
        }
    }

    private void checkTaxonomyCategory(String category) {
        if (StringUtils.isBlank(category)) {
            throw new ValidationException("Category of secondary beneficiary is mandatory");
        } else {
            String categoryCode = StringUtils.substringBeforeLast(category, "/") + "/";
            Boolean categoryCodeExists = taxonomyDao.verifyCategory(categoryCode);
            if (!Boolean.TRUE.equals(categoryCodeExists)) {
                throw new ValidationException("The category code does not exist in the archive");
            }
        }
    }
}
