package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.exception.debtposition.custom.fine.InvalidDebtPositionException;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTOWithMultiplePO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO2;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionFineValidationTest {

    private DebtPositionFineValidation debtPositionFineValidation;

    @BeforeEach
    void setUp(){
        debtPositionFineValidation = new DebtPositionFineValidation();
    }

    @Test
    void whenValidateFineThenOk(){
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);
        paymentOptionDTO1.setInstallments(List.of(buildInstallmentDTO()));
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        paymentOptionDTO2.setInstallments(List.of(buildInstallmentDTO2()));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));

        assertDoesNotThrow(() -> debtPositionFineValidation.validateFine(debtPositionDTO));
    }

    @Test
    void givenValidateFineWhenPOMoreThanTwoWithOneCancelledThenOk(){
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);
        paymentOptionDTO1.setInstallments(List.of(buildInstallmentDTO()));
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        InstallmentDTO installmentDTO = buildInstallmentDTO();
        installmentDTO.setStatus(InstallmentStatus.CANCELLED);
        paymentOptionDTO2.setInstallments(List.of(installmentDTO, buildInstallmentDTO2()));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        PaymentOptionDTO paymentOptionDTO3 = new PaymentOptionDTO();
        paymentOptionDTO3.setInstallments(List.of(buildInstallmentDTO2()));
        paymentOptionDTO3.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        paymentOptionDTO3.setStatus(PaymentOptionStatus.CANCELLED);

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));

        assertDoesNotThrow(() -> debtPositionFineValidation.validateFine(debtPositionDTO));
    }

    @Test
    void givenValidateFineWhenOnePOHasMoreThenOneInstallmentThenThrowInvalidDebtPositionException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTOWithMultiplePO();

        InvalidDebtPositionException result =
                assertThrows(InvalidDebtPositionException.class, () -> debtPositionFineValidation.validateFine(debtPositionDTO));

        assertEquals("PaymentOption with index 2 has more than one Installment", result.getMessage());
    }

    @Test
    void givenValidateFineWhenPOIsOnlyOneThenThrowInvalidDebtPositionException(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        InvalidDebtPositionException result =
                assertThrows(InvalidDebtPositionException.class, () -> debtPositionFineValidation.validateFine(debtPositionDTO));

        assertEquals("DebtPosition cannot have 1 payment options", result.getMessage());
    }

    @Test
    void givenValidateFineWhen2POAnd2InstallmentsButTypeNotReducedOrSingleThenThrowInvalidDebtPositionException(){
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.INSTALLMENTS);
        paymentOptionDTO1.setInstallments(List.of(buildInstallmentDTO()));
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        paymentOptionDTO2.setInstallments(List.of(buildInstallmentDTO2()));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.INSTALLMENTS);

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));

        InvalidDebtPositionException result =
                assertThrows(InvalidDebtPositionException.class, () -> debtPositionFineValidation.validateFine(debtPositionDTO));

        Set<PaymentOptionTypeEnum> expectedTypes = Set.of(
                PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT,
                PaymentOptionTypeEnum.SINGLE_INSTALLMENT
        );

        assertEquals(String.format("Payment options must be exactly of types: %s; provided: [INSTALLMENTS]", expectedTypes), result.getMessage());
    }
}
