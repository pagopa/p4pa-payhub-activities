package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.TaxonomyDao;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.TransferFaker.buildTransferDTO;
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

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Debt position type organization is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenDebtPositionTypeNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setDebtPositionType(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Debt position type organization is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenDebtPositionTypeCodeNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().getDebtPositionType().setCode(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Debt position type organization is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenPaymentOptionsNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.setPaymentOptions(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Debt position payment options is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenInstallmentListEmptyThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of());

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("At least one installment of the debt position is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenInstallmentWithoutRemittanceInfoThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setRemittanceInformation(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Remittance information is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenInstallmentWithDueDateRetroactiveThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setDueDate(LocalDate.of(2024, 11, 30));

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The due date cannot be retroactive", invalidValueException.getMessage());
    }

    @Test
    void givenInstallmentWithDueDateNullButMandatoryThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setFlagMandatoryDueDate(true);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setDueDate(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The due date is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenInstallmentWithAmountNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setFlagMandatoryDueDate(false);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setDueDate(null);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setAmount(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Amount is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenInstallmentWithAmountInvalidThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setFlagMandatoryDueDate(false);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setAmount(-200L);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Amount is not valid", invalidValueException.getMessage());
    }

    @Test
    void givenInstallmentWithAmountInvalidForDebtPositionTypeThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setAmount(200L);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setAmount(100L);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Amount is not valid for this debt position type org", invalidValueException.getMessage());
    }

    @Test
    void givenPersonNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setAmount(100L);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setAmount(100L);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setPayer(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The debtor is mandatory for installment", invalidValueException.getMessage());
    }

    @Test
    void givenPersonWithAnonimousCFButNotAnonymousFlagThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setAmount(null);
        debtPositionDTO.getDebtPositionTypeOrg().setFlagAnonymousFiscalCode(false);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getPayer().setUniqueIdentifierCode("ANONIMO");

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("This organization installment type or installment does not allow an anonymous unique identification code", invalidValueException.getMessage());
    }

    @Test
    void givenPersonWithNullCFButNotAnonymousFlagForDebtTypeThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setFlagAnonymousFiscalCode(true);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getPayer().setUniqueIdentifierCode(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Unique identification code is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenPersonWithNullFullNameThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getDebtPositionTypeOrg().setFlagAnonymousFiscalCode(true);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getPayer().setFullName(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Beneficiary name is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenPersonWithNullEmailThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getPayer().setEmail(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Email is not valid", invalidValueException.getMessage());
    }

    @Test
    void givenPersonWithInvalidEmailThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getPayer().setEmail("test&it");

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Email is not valid", invalidValueException.getMessage());
    }

    @Test
    void givenNoTransfersThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().setTransfers(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("At least one transfer is mandatory for installment", invalidValueException.getMessage());
    }

    @Test
    void givenTransfersMismatchThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getTransfers().add(secondTransfer);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Mismatch with transfers list", invalidValueException.getMessage());
    }

    @Test
    void givenSecondTransferPIVANullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode(null);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getTransfers().add(secondTransfer);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Fiscal code of secondary beneficiary is not valid", invalidValueException.getMessage());
    }

    @Test
    void givenSecondTransferPIVANotValidThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("00000000001");
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getTransfers().add(secondTransfer);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Fiscal code of secondary beneficiary is not valid", invalidValueException.getMessage());
    }

    @Test
    void givenSecondTransferIbanNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("31798530361");
        secondTransfer.setIban(null);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getTransfers().add(secondTransfer);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Iban of secondary beneficiary is not valid", invalidValueException.getMessage());
    }

    @Test
    void givenSecondTransferCategoryNullThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("31798530361");
        secondTransfer.setIban("IT00A0000001234567891234567");
        secondTransfer.setCategory(null);
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getTransfers().add(secondTransfer);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("Category of secondary beneficiary is mandatory", invalidValueException.getMessage());
    }

    @Test
    void givenSecondTransferCategoryNotFoundThenThrowValidationException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        TransferDTO secondTransfer = buildTransferDTO();
        secondTransfer.setTransferIndex(2);
        secondTransfer.setOrgFiscalCode("31798530361");
        secondTransfer.setIban("IT00A0000001234567891234567");
        secondTransfer.setCategory("category");
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getTransfers().add(secondTransfer);

        when(taxonomyDaoMock.verifyCategory("category/")).thenReturn(null);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The category code does not exist in the archive", invalidValueException.getMessage());
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
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getTransfers().add(secondTransfer);

        when(taxonomyDaoMock.verifyCategory("category/")).thenReturn(Boolean.TRUE);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The amount of secondary beneficiary is not valid", invalidValueException.getMessage());
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
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getTransfers().add(secondTransfer);

        when(taxonomyDaoMock.verifyCategory("category/")).thenReturn(Boolean.TRUE);

        InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () -> activity.validate(debtPositionDTO));
        assertEquals("The amount of secondary beneficiary is not valid", invalidValueException.getMessage());
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
        debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst().getTransfers().add(secondTransfer);

        when(taxonomyDaoMock.verifyCategory("category/")).thenReturn(Boolean.TRUE);

        assertDoesNotThrow(() -> activity.validate(debtPositionDTO));
    }

    @Test
    void testValidateThenSuccess(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        assertDoesNotThrow(() -> activity.validate(debtPositionDTO));
    }
}

