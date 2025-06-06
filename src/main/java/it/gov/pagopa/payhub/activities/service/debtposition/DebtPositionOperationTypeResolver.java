package it.gov.pagopa.payhub.activities.service.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.SyncCompleteDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Lazy
@Component
public class DebtPositionOperationTypeResolver {

    private final InstallmentOperationTypeResolver installmentOperationTypeResolver;

    public DebtPositionOperationTypeResolver(InstallmentOperationTypeResolver installmentOperationTypeResolver) {
        this.installmentOperationTypeResolver = installmentOperationTypeResolver;
    }

    public PaymentEventType calculateDebtPositionOperationType(DebtPositionDTO debtPositionDTO, Map<String, SyncCompleteDTO> iupdSyncStatusUpdateDTOMap) {

        if (iupdSyncStatusUpdateDTOMap.isEmpty()){
            return null;
        }

        if (hasActiveInstallmentsAlreadySynchronized(debtPositionDTO)){
            return PaymentEventType.DP_UPDATED;
        } else {
            List<InstallmentDTO> installmentsSynchronized = debtPositionDTO.getPaymentOptions().stream()
                    .flatMap(po -> po.getInstallments().stream())
                    .filter(i -> InstallmentStatus.TO_SYNC.equals(i.getStatus()) &&
                            iupdSyncStatusUpdateDTOMap.containsKey(i.getIud()))
                    .toList();

            if (installmentsSynchronized.isEmpty()) {
                return null;
            }

            List<PaymentEventType> operations = installmentsSynchronized.stream()
                    .map(installmentOperationTypeResolver::calculateInstallmentOperationType)
                    .filter(Objects::nonNull)
                    .toList();

            if (operations.stream().allMatch(op -> op.equals(PaymentEventType.DP_CREATED))) {
                return PaymentEventType.DP_CREATED;
            } else if (operations.stream().allMatch(op -> op.equals(PaymentEventType.DP_CANCELLED))) {
                return PaymentEventType.DP_CANCELLED;
            } else {
                return PaymentEventType.DP_UPDATED;
            }
        }
    }

    public boolean hasActiveInstallmentsAlreadySynchronized(DebtPositionDTO debtPositionDTO) {
        // If exists at least one active (not CANCELLED or INVALID) Installment not TO_SYNC
        return debtPositionDTO.getPaymentOptions().stream()
                .flatMap(po -> po.getInstallments().stream())
                .anyMatch(i -> !List.of(
                        InstallmentStatus.TO_SYNC,
                        InstallmentStatus.INVALID,
                        InstallmentStatus.CANCELLED
                ).contains(i.getStatus()));
    }
}
