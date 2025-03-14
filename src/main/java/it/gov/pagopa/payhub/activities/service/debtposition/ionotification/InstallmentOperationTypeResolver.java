package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSyncStatus;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class InstallmentOperationTypeResolver {

    public NotificationRequestDTO.OperationTypeEnum calculateInstallmentOperationType(InstallmentDTO installment) {
        InstallmentSyncStatus syncStatus = installment.getSyncStatus();
        if (syncStatus == null) return null;

        InstallmentSyncStatus.SyncStatusFromEnum fromStatus = syncStatus.getSyncStatusFrom();
        InstallmentSyncStatus.SyncStatusToEnum toStatus = syncStatus.getSyncStatusTo();

        return switch (toStatus) {
            case UNPAID -> (fromStatus == InstallmentSyncStatus.SyncStatusFromEnum.DRAFT)
                    ? NotificationRequestDTO.OperationTypeEnum.CREATE_DP
                    : NotificationRequestDTO.OperationTypeEnum.UPDATE_DP;
            case INVALID, EXPIRED -> NotificationRequestDTO.OperationTypeEnum.UPDATE_DP;
            case CANCELLED -> NotificationRequestDTO.OperationTypeEnum.DELETE_DP;
            default -> null;
        };
    }
}
