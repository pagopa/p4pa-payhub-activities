package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.TaxonomyDao;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static it.gov.pagopa.payhub.activities.utility.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.utility.faker.TransferFaker.buildTransferDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateDebtPositionActivityImplTest {

    private ValidateDebtPositionActivity activity;

    @Mock private TaxonomyDao taxonomyDaoMock;

    @BeforeEach
    void init() {
        activity = new ValidateDebtPositionActivityImpl(taxonomyDaoMock);
    }

    @Test
    void givenDebtPositionTypeOrgNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.setDebtPositionTypeOrg(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Debt position type organization is mandatory", validationException.getMessage());
    }

    @Test
    void givenDebtPositionTypeNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setDebtPositionType(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Debt position type organization is mandatory", validationException.getMessage());
    }

    @Test
    void givenDebtPositionTypeCodeNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().getDebtPositionType().setCode(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Debt position type organization is mandatory", validationException.getMessage());
    }

    @Test
    void givenPaymentOptionsNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.setPaymentOptions(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Debt position payment options is mandatory", validationException.getMessage());
    }

    @Test
    void givenInstallmentListEmptyThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().get(0).setInstallments(List.of());

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("At least one installment of the debt position is mandatory", validationException.getMessage());
    }

    @Test
    void givenInstallmentWithoutRemittanceInfoThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).setRemittanceInformation(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Remittance information is mandatory", validationException.getMessage());
    }

    @Test
    void givenInstallmentWithDueDateRetroactiveThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).setDueDate(LocalDate.of(2024, 11, 30));

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The due date cannot be retroactive", validationException.getMessage());
    }

    @Test
    void givenInstallmentWithDueDateNullButMandatoryThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setFlagMandatoryDueDate(true);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).setDueDate(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The due date is mandatory", validationException.getMessage());
    }

    @Test
    void givenInstallmentWithAmountNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setFlagMandatoryDueDate(false);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).setDueDate(null);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).setAmount(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Amount is mandatory", validationException.getMessage());
    }

    @Test
    void givenInstallmentWithAmountInvalidThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setFlagMandatoryDueDate(false);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).setAmount(-200L);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Amount is not valid", validationException.getMessage());
    }

    @Test
    void givenInstallmentWithAmountInvalidForDebtPositionTypeThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setAmount(200L);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).setAmount(100L);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Amount is not valid for this debt position type org", validationException.getMessage());
    }

    @Test
    void givenPersonNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setAmount(100L);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).setAmount(100L);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).setPayer(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The debtor is mandatory for installment", validationException.getMessage());
    }

    @Test
    void givenPersonWithAnonimousCFButNotAnonymousFlagThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setAmount(null);
        debtPositionDTO.getDebtPositionTypeOrg().setFlagAnonymousFiscalCode(false);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getPayer().setUniqueIdentifierCode("ANONIMO");

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("This organization installment type or installment does not allow an anonymous unique identification code", validationException.getMessage());
    }

    @Test
    void givenPersonWithNullCFButNotAnonymousFlagForDebtTypeThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setFlagAnonymousFiscalCode(true);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getPayer().setUniqueIdentifierCode(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Unique identification code is mandatory", validationException.getMessage());
    }

    @Test
    void givenPersonWithNullFullNameThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setFlagAnonymousFiscalCode(true);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getPayer().setFullName(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Beneficiary name is mandatory", validationException.getMessage());
    }

    @Test
    void givenPersonWithNullEmailThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getPayer().setEmail(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Email is not valid", validationException.getMessage());
    }

    @Test
    void givenPersonWithInvalidEmailThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getPayer().setEmail("test&it");

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Email is not valid", validationException.getMessage());
    }

    @Test
    void givenNoTransfersThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).setTransfers(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("At least one transfer is mandatory for installment", validationException.getMessage());
    }

    @Test
    void givenTransfersMismatchThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getTransfers().add(secondTransfer);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Mismatch with transfers list", validationException.getMessage());
    }

    @Test
    void givenSecondTransferPIVANullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode(null);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getTransfers().add(secondTransfer);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Fiscal code of secondary beneficiary is not valid", validationException.getMessage());
    }

    @Test
    void givenSecondTransferPIVANotValidThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("00000000001");
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getTransfers().add(secondTransfer);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Fiscal code of secondary beneficiary is not valid", validationException.getMessage());
    }

    @Test
    void givenSecondTransferIbanNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("31798530361");
        secondTransfer.setIban(null);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getTransfers().add(secondTransfer);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Iban of secondary beneficiary is not valid", validationException.getMessage());
    }

    @Test
    void givenSecondTransferCategoryNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("31798530361");
        secondTransfer.setIban("IT00A0000001234567891234567");
        secondTransfer.setCategory(null);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getTransfers().add(secondTransfer);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Category of secondary beneficiary is mandatory", validationException.getMessage());
    }

    @Test
    void givenSecondTransferCategoryNotFoundThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("31798530361");
        secondTransfer.setIban("IT00A0000001234567891234567");
        secondTransfer.setCategory("category");
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getTransfers().add(secondTransfer);

        when(taxonomyDaoMock.verifyCategory("category/")).thenReturn(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The category code does not exist in the archive", validationException.getMessage());
    }

    @Test
    void givenSecondTransferAmountNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("31798530361");
        secondTransfer.setIban("IT00A0000001234567891234567");
        secondTransfer.setCategory("category");
        secondTransfer.setAmount(null);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getTransfers().add(secondTransfer);

        when(taxonomyDaoMock.verifyCategory("category/")).thenReturn(Boolean.TRUE);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The amount of secondary beneficiary is not valid", validationException.getMessage());
    }

    @Test
    void givenSecondTransferAmountNegativeThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("31798530361");
        secondTransfer.setIban("IT00A0000001234567891234567");
        secondTransfer.setCategory("category");
        secondTransfer.setAmount(-12L);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getTransfers().add(secondTransfer);

        when(taxonomyDaoMock.verifyCategory("category/")).thenReturn(Boolean.TRUE);

        ValidationException validationException = assertThrows(ValidationException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The amount of secondary beneficiary is not valid", validationException.getMessage());
    }

    @Test
    void givenSecondTransferThenSuccess(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("31798530361");
        secondTransfer.setIban("IT00A0000001234567891234567");
        secondTransfer.setCategory("category");
        secondTransfer.setAmount(12L);
        debtPositionDTO.getPaymentOptions().get(0).getInstallments().get(0).getTransfers().add(secondTransfer);

        when(taxonomyDaoMock.verifyCategory("category/")).thenReturn(Boolean.TRUE);

        assertDoesNotThrow(() -> activity.validate(debtPositionDTO));
    }

    @Test
    void testValidateThenSuccess(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        assertDoesNotThrow(() -> activity.validate(debtPositionDTO));
    }
}

