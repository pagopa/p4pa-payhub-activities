package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Lazy
@Service
public class FinalizeDebtPositionSyncStatusActivityImpl implements FinalizeDebtPositionSyncStatusActivity {

    private final DebtPositionService debtPositionService;

    public FinalizeDebtPositionSyncStatusActivityImpl(DebtPositionService debtPositionService) {
        this.debtPositionService = debtPositionService;
    }

    @Override
    public DebtPositionDTO finalizeDebtPositionSyncStatus(Long debtPositionId, Map<String, IupdSyncStatusUpdateDTO> syncStatusDTO) {
        return debtPositionService.finalizeSyncStatus(debtPositionId, syncStatusDTO);
    }

}
