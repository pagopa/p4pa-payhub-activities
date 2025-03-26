package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.DebtPositionFineResponse;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.FineWfExecutionConfig;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionTypeEnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Lazy
@Service
public class DebtPositionFineDatesProcessor {

    public DebtPositionFineResponse processFine(DebtPositionDTO debtPositionDTO, FineWfExecutionConfig executionParams) {
        DebtPositionFineResponse response = new DebtPositionFineResponse();
        OffsetDateTime now = OffsetDateTime.now();

        boolean notified = processPaymentOptions(debtPositionDTO, executionParams, response, now);

        response.setNotified(notified);
        return response;
    }

    private boolean processPaymentOptions(DebtPositionDTO debtPositionDTO, FineWfExecutionConfig executionParams, DebtPositionFineResponse response, OffsetDateTime now) {
        return debtPositionDTO.getPaymentOptions().stream()
                .flatMap(po -> po.getInstallments().stream()
                        .filter(installment -> installment.getNotificationDate() != null)
                        .map(installment -> {
                            OffsetDateTime notificationDate = installment.getNotificationDate();

                            if (PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT.equals(po.getPaymentOptionType())) {
                                return processReducedSingleInstallment(executionParams, installment, notificationDate, response, now);

                            } else if (PaymentOptionTypeEnum.SINGLE_INSTALLMENT.equals(po.getPaymentOptionType())) {
                                return processSingleInstallment(executionParams, installment, notificationDate, now);
                            }

                            return false;
                        }))
                .anyMatch(Boolean::booleanValue);
    }

    private boolean processSingleInstallment(FineWfExecutionConfig executionParams, InstallmentDTO installment, OffsetDateTime notificationDate, OffsetDateTime now) {
        OffsetDateTime expirationEndDate = notificationDate.plusDays(executionParams.getExpirationDays());

        return processDueDate(installment, expirationEndDate, now);
    }

    private boolean processReducedSingleInstallment(FineWfExecutionConfig executionParams, InstallmentDTO installment, OffsetDateTime notificationDate, DebtPositionFineResponse response, OffsetDateTime now) {
        OffsetDateTime reductionEndDate = notificationDate.plusDays(executionParams.getDiscountDays());
        response.setReductionEndDate(reductionEndDate);

        return processDueDate(installment, reductionEndDate, now);
    }

    private boolean processDueDate(InstallmentDTO installment, OffsetDateTime reductionEndDate, OffsetDateTime now) {
        if (!reductionEndDate.toLocalDate().equals(installment.getDueDate())) {
            installment.setDueDate(reductionEndDate.isAfter(now) ? reductionEndDate.toLocalDate() : now.toLocalDate());
            return true;
        }
        return false;
    }
}
