package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.exception.debtposition.custom.fine.InvalidDebtPositionException;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Lazy
@Service
public class DebtPositionFineValidation {

    private final Set<PaymentOptionTypeEnum> expectedTypes = Set.of(
            PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT,
            PaymentOptionTypeEnum.SINGLE_INSTALLMENT
    );

    public void validateFine(DebtPositionDTO debtPositionDTO) {
        List<PaymentOptionDTO> validPaymentOptions = validatePaymentOptionStatusAndCount(debtPositionDTO);
        validateInstallmentsStatusAndCount(validPaymentOptions);
        validatePaymentOptionTypes(validPaymentOptions);
    }

    /**
     * Validates and returns the list of payment options with status different from CANCELLED.
     * Ensures that there are exactly two payment options.
     */
    private List<PaymentOptionDTO> validatePaymentOptionStatusAndCount(DebtPositionDTO debtPositionDTO) {
        List<PaymentOptionDTO> paymentOptionDTOList = debtPositionDTO.getPaymentOptions().stream()
                .filter(po -> po.getStatus() != PaymentOptionStatus.CANCELLED)
                .toList();

        if (paymentOptionDTOList.size() != 2) {
            throw new InvalidDebtPositionException(String.format("DebtPosition cannot have %s payment options", paymentOptionDTOList.size()));
        }

        return paymentOptionDTOList;
    }

    /**
     * Validates that each provided payment option has exactly one non-CANCELLED installment.
     */
    private void validateInstallmentsStatusAndCount(List<PaymentOptionDTO> paymentOptionDTOList) {
        for (PaymentOptionDTO paymentOptionDTO : paymentOptionDTOList) {
            long activeInstallments = paymentOptionDTO.getInstallments().stream()
                    .filter(i -> i.getStatus() != InstallmentStatus.CANCELLED)
                    .count();

            if (activeInstallments != 1) {
                throw new InvalidDebtPositionException("PaymentOption has more than one Installment");
            }
        }
    }

    /**
     * Validates that the types of the given payment options match the expected types
     * for the fine(REDUCED_SINGLE_INSTALLMENT and SINGLE_INSTALLMENT).
     */
    private void validatePaymentOptionTypes(List<PaymentOptionDTO> validPaymentOptions) {
        Set<PaymentOptionTypeEnum> actualTypes = validPaymentOptions.stream()
                .map(PaymentOptionDTO::getPaymentOptionType)
                .collect(Collectors.toSet());

        if (!actualTypes.equals(expectedTypes)) {
            throw new InvalidDebtPositionException("Payment options must be exactly of types: REDUCED_SINGLE_INSTALLMENT and SINGLE_INSTALLMENT");
        }
    }
}
