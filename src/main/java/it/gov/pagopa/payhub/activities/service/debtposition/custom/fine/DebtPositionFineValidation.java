package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionTypeEnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
public class DebtPositionFineValidation {

    public boolean validateFine(DebtPositionDTO debtPositionDTO) {
        if (debtPositionDTO.getPaymentOptions().size() != 2) {
            return false;
        }

        List<PaymentOptionDTO> validOptions = debtPositionDTO.getPaymentOptions().stream()
                .filter(po -> po.getInstallments().size() == 1 && po.getStatus() != PaymentOptionStatus.CANCELLED)
                .toList();

        List<PaymentOptionTypeEnum> types = validOptions.stream()
                .map(PaymentOptionDTO::getPaymentOptionType)
                .toList();

        return types.contains(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT)
                && types.contains(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
    }
}
