package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DebtPositionUtilities {
    private DebtPositionUtilities(){}

    private static final String AUX_DIGIT = "3";
    private static final Set<InstallmentStatus> expirableStatuses = Set.of(
            InstallmentStatus.UNPAID
    );
    public static final Set<InstallmentStatus> INSTALLMENT_PAYED_STATUSES_SET = Set.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);
    public static final List<InstallmentStatus> INSTALLMENT_PAYED_STATUSES_LIST = List.copyOf(INSTALLMENT_PAYED_STATUSES_SET);

    /** It will return the min dueDate of all active installments */
    public static LocalDate calcDebtPositionNextDueDate(DebtPositionDTO debtPositionDTO){
        return debtPositionDTO.getPaymentOptions().stream()
                .flatMap(po ->po.getInstallments().stream())
                .filter(i -> Boolean.TRUE.equals(i.getSwitchToExpired()))
                .filter(i -> expirableStatuses.contains(i.getStatus()))
                .map(InstallmentDTO::getDueDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    public static String iuv2nav(String iuv) { return AUX_DIGIT + iuv;    }
}
