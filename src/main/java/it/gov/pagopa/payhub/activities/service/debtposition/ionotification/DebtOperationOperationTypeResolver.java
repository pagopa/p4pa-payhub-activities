package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Lazy
@Component
public class DebtOperationOperationTypeResolver {

    private final InstallmentOperationTypeResolver installmentOperationTypeResolver;

    public DebtOperationOperationTypeResolver(InstallmentOperationTypeResolver installmentOperationTypeResolver) {
        this.installmentOperationTypeResolver = installmentOperationTypeResolver;
    }

    public NotificationRequestDTO.OperationTypeEnum calculateDebtPositionOperationType(DebtPositionDTO debtPositionDTO, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap) {

        if (isUpdateOperationType(debtPositionDTO)){
            return NotificationRequestDTO.OperationTypeEnum.UPDATE_DP;
        } else {
            List<InstallmentDTO> installmentDTOList = debtPositionDTO.getPaymentOptions().stream()
                    .flatMap(po -> po.getInstallments().stream())
                    .filter(i -> InstallmentDTO.StatusEnum.TO_SYNC.equals(i.getStatus()) &&
                            iupdSyncStatusUpdateDTOMap.containsKey(i.getIud()))
                    .toList();

            if (installmentDTOList.isEmpty()) {
                return null;
            }

            List<NotificationRequestDTO.OperationTypeEnum> operations = installmentDTOList.stream()
                    .map(installmentOperationTypeResolver::calculateInstallmentOperationType)
                    .filter(Objects::nonNull)
                    .toList();

            if (operations.stream().allMatch(op -> op.equals(NotificationRequestDTO.OperationTypeEnum.CREATE_DP))) {
                return NotificationRequestDTO.OperationTypeEnum.CREATE_DP;
            } else if (operations.stream().allMatch(op -> op.equals(NotificationRequestDTO.OperationTypeEnum.DELETE_DP))) {
                return NotificationRequestDTO.OperationTypeEnum.DELETE_DP;
            } else {
                return NotificationRequestDTO.OperationTypeEnum.UPDATE_DP;
            }
        }
    }

    public boolean isUpdateOperationType(DebtPositionDTO debtPositionDTO) {
        // If not even one is TO_SYNC, INVALID o CANCELLED then is UPDATE_PD
        return debtPositionDTO.getPaymentOptions().stream()
                .flatMap(po -> po.getInstallments().stream())
                .anyMatch(i -> !List.of(
                        InstallmentDTO.StatusEnum.TO_SYNC,
                        InstallmentDTO.StatusEnum.INVALID,
                        InstallmentDTO.StatusEnum.CANCELLED
                ).contains(i.getStatus()));
    }
}
