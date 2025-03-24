package it.gov.pagopa.payhub.activities.service.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSyncStatus;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class InstallmentOperationTypeResolver {

    public PaymentEventType calculateInstallmentOperationType(InstallmentDTO installment) {
        InstallmentSyncStatus syncStatus = installment.getSyncStatus();
        if (syncStatus == null) return null;

        InstallmentStatus fromStatus = syncStatus.getSyncStatusFrom();
        InstallmentStatus toStatus = syncStatus.getSyncStatusTo();

        return switch (toStatus) {
            case UNPAID -> (fromStatus == InstallmentStatus.DRAFT)
                    ? PaymentEventType.DP_CREATED
                    : PaymentEventType.DP_UPDATED;
            case INVALID, EXPIRED -> PaymentEventType.DP_UPDATED;
            case CANCELLED -> PaymentEventType.DP_CANCELLED;
            default -> null;
        };
    }
}
