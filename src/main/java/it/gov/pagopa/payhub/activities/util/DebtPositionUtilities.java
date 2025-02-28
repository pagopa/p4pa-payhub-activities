package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public class DebtPositionUtilities {
    private DebtPositionUtilities(){}

    /** It will return the min dueDate of all active installments */
    public static LocalDate calcDebtPositionNextDueDate(DebtPositionDTO debtPositionDTO){
        return debtPositionDTO.getPaymentOptions().stream()
                .flatMap(po ->po.getInstallments().stream())
                .filter(i -> InstallmentDTO.StatusEnum.UNPAID.equals(i.getStatus()))
                .map(InstallmentDTO::getDueDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }
}
