package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTOWithMultiplePO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO2;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class DebtPositionFineValidationTest {

    private DebtPositionFineValidation debtPositionFineValidation;

    @BeforeEach
    void setUp(){
        debtPositionFineValidation = new DebtPositionFineValidation();
    }

    @Test
    void whenValidateFineThenReturnTrue(){
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);
        paymentOptionDTO1.setInstallments(List.of(buildInstallmentDTO()));
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        paymentOptionDTO2.setInstallments(List.of(buildInstallmentDTO2()));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));

        boolean result = debtPositionFineValidation.validateFine(debtPositionDTO);

        assertTrue(result);
    }

    @Test
    void givenValidateFineWhenPOMoreThanTwoWithOneCancelledThenReturnTrue(){
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);
        paymentOptionDTO1.setInstallments(List.of(buildInstallmentDTO()));
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        paymentOptionDTO2.setInstallments(List.of(buildInstallmentDTO2()));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        PaymentOptionDTO paymentOptionDTO3 = new PaymentOptionDTO();
        paymentOptionDTO3.setInstallments(List.of(buildInstallmentDTO2()));
        paymentOptionDTO3.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        paymentOptionDTO3.setStatus(PaymentOptionStatus.CANCELLED);

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));

        boolean result = debtPositionFineValidation.validateFine(debtPositionDTO);

        assertTrue(result);
    }

    @Test
    void givenValidateFineWhenOnePOHasMoreThenOneInstallmentThenReturnFalse(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTOWithMultiplePO();

        boolean result = debtPositionFineValidation.validateFine(debtPositionDTO);

        assertFalse(result);
    }

    @Test
    void givenValidateFineWhenPOIsOnlyOneThenReturnFalse(){
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        boolean result = debtPositionFineValidation.validateFine(debtPositionDTO);

        assertFalse(result);
    }

    @Test
    void givenValidateFineWhen2POAnd2InstallmentsButTypeNotReducedOrSingleThenReturnTrue(){
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.INSTALLMENTS);
        paymentOptionDTO1.setInstallments(List.of(buildInstallmentDTO()));
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        paymentOptionDTO2.setInstallments(List.of(buildInstallmentDTO2()));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.INSTALLMENTS);

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));

        boolean result = debtPositionFineValidation.validateFine(debtPositionDTO);

        assertFalse(result);
    }
}
