package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.PaymentOptionService;
import it.gov.pagopa.payhub.activities.dto.debtposition.HandleFineDebtPositionResult;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Lazy
@Slf4j
@Service
public class DebtPositionFineProcessor {

    private final InstallmentService installmentService;
    private final PaymentOptionService paymentOptionService;

    public DebtPositionFineProcessor(InstallmentService installmentService, PaymentOptionService paymentOptionService) {
        this.installmentService = installmentService;
        this.paymentOptionService = paymentOptionService;
    }

    public void processFine(HandleFineDebtPositionResult handleFineDebtPositionResult) {

        // If we are currently on the reduction period
        if (handleFineDebtPositionResult.getReductionEndDate() == null || handleFineDebtPositionResult.getReductionEndDate().isAfter(OffsetDateTime.now())) {
            updateFullPOAndInstallmentsIfToSync(handleFineDebtPositionResult.getDebtPositionDTO());
        }
    }

    private void updateFullPOAndInstallmentsIfToSync(DebtPositionDTO debtPositionDTO) {
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
                        po.setStatus(PaymentOptionStatus.UNPAYABLE);

                        log.info("Updating PaymentOption with id: {} to status: {}", po.getPaymentOptionId(), po.getStatus());
                        paymentOptionService.updateStatus(po.getPaymentOptionId(), PaymentOptionStatus.UNPAYABLE);

                        unpaidInstallments.forEach(installmentDTO -> {
                            installmentDTO.setStatus(InstallmentStatus.UNPAYABLE);
                            installmentDTO.setSyncStatus(null);

                            log.info("Setting Installment with id: {} to status: {} and syncStatus: null", installmentDTO.getInstallmentId(), installmentDTO.getStatus());
                            installmentService.updateStatusAndSyncStatus(installmentDTO.getInstallmentId(), InstallmentStatus.UNPAYABLE, null);
                        });
                    }
                });
    }
}
