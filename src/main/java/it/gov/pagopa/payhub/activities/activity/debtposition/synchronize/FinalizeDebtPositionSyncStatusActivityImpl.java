package it.gov.pagopa.payhub.activities.activity.debtposition.synchronize;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.SyncErrorDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.SyncStatusUpdateRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Lazy
@Service
public class FinalizeDebtPositionSyncStatusActivityImpl implements FinalizeDebtPositionSyncStatusActivity {

    private final DebtPositionService debtPositionService;

    public FinalizeDebtPositionSyncStatusActivityImpl(DebtPositionService debtPositionService) {
        this.debtPositionService = debtPositionService;
    }

    @Override
    public DebtPositionDTO finalizeDebtPositionSyncStatus(Long debtPositionId, SyncStatusUpdateRequestDTO syncStatusDTO) {
        log.info("Finalizing TO_SYNC status of DebtPosition {}: iupd2finalize:{}; iupdSyncError: {}",
                debtPositionId,
                mapToString(syncStatusDTO.getIupd2finalize(), i -> i.getNewStatus().toString()),
                mapToString(syncStatusDTO.getIupdSyncError(), SyncErrorDTO::getErrorDescription)
        );
        return debtPositionService.finalizeSyncStatus(debtPositionId, syncStatusDTO);
    }

    private <T> String mapToString(Map<String, T> map, Function<T, String> entry2String) {
        return CollectionUtils.isEmpty(map)
                ? ""
                : map.entrySet().stream()
                .map(e -> e.getKey() + ":" + entry2String.apply(e.getValue()))
                .collect(Collectors.joining(", "));
    }
}
