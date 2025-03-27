package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.dto.debtposition.HandleFineDebtPositionResult;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.FineWfExecutionConfig;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionTypeEnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;

@Lazy
@Service
public class DebtPositionFineProcessor {

    private final DebtPositionFineNotificationDateProcessor dateProcessor;

    public DebtPositionFineProcessor(DebtPositionFineNotificationDateProcessor dateProcessor) {
        this.dateProcessor = dateProcessor;
    }

    public HandleFineDebtPositionResult processFine(DebtPositionDTO debtPositionDTO, FineWfExecutionConfig executionParams) {
        HandleFineDebtPositionResult handleFineDebtPositionResult = dateProcessor.processNotificationDate(debtPositionDTO, executionParams);

        if (!handleFineDebtPositionResult.getReductionEndDate().isBefore(OffsetDateTime.now())) {
            DebtPositionDTO debtPosition = updatePOAndInstallmentsIfNotPayable(debtPositionDTO);
            if (debtPosition != null){
                handleFineDebtPositionResult.setDebtPositionDTO(debtPosition);
            }
        }

        return handleFineDebtPositionResult;
    }

    private DebtPositionDTO updatePOAndInstallmentsIfNotPayable(DebtPositionDTO debtPositionDTO) {
        return debtPositionDTO.getPaymentOptions().stream()
                .filter(po ->
                        PaymentOptionTypeEnum.SINGLE_INSTALLMENT.equals(po.getPaymentOptionType()) &&
                        PaymentOptionStatus.TO_SYNC.equals(po.getStatus()))
                .findFirst()
                .map(po -> {
                    boolean anyUnpaidInstallment = po.getInstallments().stream()
                            .anyMatch(installmentDTO ->
                                    InstallmentStatus.TO_SYNC.equals(installmentDTO.getStatus()) && 
                                    InstallmentStatus.UNPAID.equals(Objects.requireNonNull(installmentDTO.getSyncStatus()).getSyncStatusTo()));

                    if (anyUnpaidInstallment) {
                        // TODO save to db
                        po.setStatus(PaymentOptionStatus.UNPAYABLE);
                        po.getInstallments().forEach(inst -> inst.setStatus(InstallmentStatus.UNPAYABLE));
                    }

                    return debtPositionDTO;
                })
                .orElse(null);
    }
}
