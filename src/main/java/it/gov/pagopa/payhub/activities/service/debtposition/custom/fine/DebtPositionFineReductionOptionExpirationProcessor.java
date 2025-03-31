package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.PaymentOptionService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class DebtPositionFineReductionOptionExpirationProcessor {

    private final DebtPositionService debtPositionService;
    private final PaymentOptionService paymentOptionService;
    private final InstallmentService installmentService;

    public DebtPositionFineReductionOptionExpirationProcessor(DebtPositionService debtPositionService, PaymentOptionService paymentOptionService, InstallmentService installmentService) {
        this.debtPositionService = debtPositionService;
        this.paymentOptionService = paymentOptionService;
        this.installmentService = installmentService;
    }

    public DebtPositionDTO handleFineReductionExpiration(Long debtPositionId) {
        DebtPositionDTO debtPositionDTO = debtPositionService.getDebtPosition(debtPositionId);

        // If debt position status is not paid or reported update payment option status and it's installment
        if (!DebtPositionStatus.PAID.equals(debtPositionDTO.getStatus()) || !DebtPositionStatus.REPORTED.equals(debtPositionDTO.getStatus())) {
            debtPositionDTO.getPaymentOptions().stream()
                    .filter(po -> PaymentOptionTypeEnum.SINGLE_INSTALLMENT.equals(po.getPaymentOptionType()))
                    .forEach(paymentOptionDTO -> {
                        paymentOptionDTO.setStatus(PaymentOptionStatus.TO_SYNC);

                        log.info("Updating PaymentOption with id: {} to status: {}", paymentOptionDTO.getPaymentOptionId(), paymentOptionDTO.getStatus());
                        paymentOptionService.updateStatus(paymentOptionDTO.getPaymentOptionId(), PaymentOptionStatus.TO_SYNC);

                        paymentOptionDTO.getInstallments().forEach(installmentDTO -> {
                            installmentDTO.setStatus(InstallmentStatus.TO_SYNC);
                            InstallmentSyncStatus syncStatus = new InstallmentSyncStatus();
                            syncStatus.setSyncStatusFrom(InstallmentStatus.UNPAYABLE);
                            syncStatus.setSyncStatusTo(InstallmentStatus.UNPAID);
                            installmentDTO.setSyncStatus(syncStatus);

                            log.info("Setting Installment with id: {} to status: {} and syncStatus: {}", installmentDTO.getInstallmentId(), installmentDTO.getStatus(), syncStatus);
                            installmentService.updateStatusAndSyncStatus(installmentDTO.getInstallmentId(), InstallmentStatus.TO_SYNC, syncStatus);
                        });
                    });

            return debtPositionDTO;

        } else {
            return null;
        }
    }
}
