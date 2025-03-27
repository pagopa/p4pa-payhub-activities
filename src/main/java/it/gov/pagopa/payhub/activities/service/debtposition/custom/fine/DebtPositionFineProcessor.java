package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.dto.debtposition.HandleFineDebtPositionResult;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.FineWfExecutionConfig;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Lazy
@Service
public class DebtPositionFineProcessor {

    private final DebtPositionFineNotificationDateProcessor notificationDateProcessor;

    public DebtPositionFineProcessor(DebtPositionFineNotificationDateProcessor notificationDateProcessor) {
        this.notificationDateProcessor = notificationDateProcessor;
    }

    public HandleFineDebtPositionResult processFine(DebtPositionDTO debtPositionDTO, FineWfExecutionConfig executionParams) {
        HandleFineDebtPositionResult handleFineDebtPositionResult = notificationDateProcessor.processNotificationDate(debtPositionDTO, executionParams);

        if (handleFineDebtPositionResult.getReductionEndDate() == null || handleFineDebtPositionResult.getReductionEndDate().isAfter(OffsetDateTime.now())) {
            updatePOAndInstallmentsIfNotPayable(debtPositionDTO, handleFineDebtPositionResult);
        }

        return handleFineDebtPositionResult;
    }

    private void updatePOAndInstallmentsIfNotPayable(DebtPositionDTO debtPositionDTO, HandleFineDebtPositionResult handleFineDebtPositionResult) {
        debtPositionDTO.getPaymentOptions().stream()
                .filter(po ->
                        PaymentOptionTypeEnum.SINGLE_INSTALLMENT.equals(po.getPaymentOptionType()) &&
                        PaymentOptionStatus.TO_SYNC.equals(po.getStatus()))
                .findFirst()
                .ifPresent(po -> {
                    List<InstallmentDTO> unpaidInstallments = po.getInstallments().stream()
                            .filter(inst ->
                                    InstallmentStatus.TO_SYNC.equals(inst.getStatus()) &&
                                    InstallmentStatus.UNPAID.equals(Objects.requireNonNull(inst.getSyncStatus()).getSyncStatusTo()))
                            .toList();

                    if (!unpaidInstallments.isEmpty()) {
                        // TODO save to db
                        po.setStatus(PaymentOptionStatus.UNPAYABLE);
                        unpaidInstallments.forEach(inst -> inst.setStatus(InstallmentStatus.UNPAYABLE));
                        handleFineDebtPositionResult.setDebtPositionDTO(debtPositionDTO);
                    }
                });
    }
}
