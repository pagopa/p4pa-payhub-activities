package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.dto.debtposition.HandleFineDebtPositionResult;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.FineWfExecutionConfig;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionTypeEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

import static it.gov.pagopa.payhub.activities.util.Utilities.toOffsetDateTimeEndOfTheDay;

@Lazy
@Service
public class DebtPositionFineNotificationDateProcessor {

    public HandleFineDebtPositionResult processNotificationDate(DebtPositionDTO debtPositionDTO, FineWfExecutionConfig executionParams) {
        HandleFineDebtPositionResult response = new HandleFineDebtPositionResult();

        boolean notified = processPaymentOptions(debtPositionDTO, executionParams, response);

        response.setNotified(notified);
        response.setDebtPositionDTO(debtPositionDTO);
        return response;
    }

    private boolean processPaymentOptions(DebtPositionDTO debtPositionDTO, FineWfExecutionConfig executionParams, HandleFineDebtPositionResult response) {
        boolean[] notified = {false};

        debtPositionDTO.getPaymentOptions().forEach(po -> po.getInstallments().stream()
                .filter(installment -> installment.getNotificationDate() != null)
                .forEach(installment -> {
                    boolean processed = false;
                    if (PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT.equals(po.getPaymentOptionType())) {
                        processed = processNotifiedReducedSingleInstallment(executionParams, installment, response);
                    } else if (PaymentOptionTypeEnum.SINGLE_INSTALLMENT.equals(po.getPaymentOptionType())) {
                        processed = processNotifiedSingleInstallment(executionParams, installment);
                    }

                    notified[0] |= processed;
                }));

        return notified[0];
    }

    private boolean processNotifiedReducedSingleInstallment(FineWfExecutionConfig executionParams, InstallmentDTO installment, HandleFineDebtPositionResult response) {
        boolean notified = processDueDate(installment, executionParams.getDiscountDays());
        response.setReductionEndDate(toOffsetDateTimeEndOfTheDay(installment.getDueDate()));
        return notified;
    }

    private boolean processNotifiedSingleInstallment(FineWfExecutionConfig executionParams, InstallmentDTO installment) {
        return processDueDate(installment, executionParams.getExpirationDays());
    }

    private boolean processDueDate(InstallmentDTO installment, long days2add) {
        LocalDate nextDueDate = Objects.requireNonNull(installment.getNotificationDate()).plusDays(days2add).atZoneSameInstant(Utilities.ZONEID).toLocalDate();
        if (!nextDueDate.equals(installment.getDueDate())) {
            // TODO save the debt position updated
                installment.setDueDate(ObjectUtils.max(nextDueDate, LocalDate.now()));
                return true;
            }

        return false;
    }
}
