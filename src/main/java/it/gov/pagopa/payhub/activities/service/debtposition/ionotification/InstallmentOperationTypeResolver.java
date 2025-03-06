package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSyncStatus;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Lazy
@Component
public class InstallmentOperationTypeResolver {

    public NotificationRequestDTO.OperationTypeEnum calculateOperationType(List<InstallmentDTO> installmentDTOList) {
        return installmentDTOList.stream()
                .map(InstallmentDTO::getSyncStatus)
                .filter(Objects::nonNull)
                .map(syncStatus -> {
                    InstallmentSyncStatus.SyncStatusFromEnum fromStatus = syncStatus.getSyncStatusFrom();
                    InstallmentSyncStatus.SyncStatusToEnum toStatus = syncStatus.getSyncStatusTo();

                    return switch (toStatus) {
                        case UNPAID -> (fromStatus.equals(InstallmentSyncStatus.SyncStatusFromEnum.DRAFT))
                                ? NotificationRequestDTO.OperationTypeEnum.CREATE_DP
                                : NotificationRequestDTO.OperationTypeEnum.UPDATE_DP;
                        case INVALID, EXPIRED -> NotificationRequestDTO.OperationTypeEnum.UPDATE_DP;
                        case CANCELLED -> NotificationRequestDTO.OperationTypeEnum.DELETE_DP;
                        default -> null;
                    };
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
