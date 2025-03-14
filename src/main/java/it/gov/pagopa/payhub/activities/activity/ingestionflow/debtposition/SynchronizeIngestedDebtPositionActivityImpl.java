package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.service.debtposition.DebtPositionSearchService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Lazy
@Service
public class SynchronizeIngestedDebtPositionActivityImpl implements SynchronizeIngestedDebtPositionActivity {

    private final DebtPositionSearchService debtPositionSearchService;

    public SynchronizeIngestedDebtPositionActivityImpl(DebtPositionSearchService debtPositionSearchService) {
        this.debtPositionSearchService = debtPositionSearchService;
    }

    @Override
    public String synchronizeIngestedDebtPosition(Long ingestionFlowFileId) {
        log.info("Synchronizing all debt positions related to ingestion flow file id {}", ingestionFlowFileId);
        List<DebtPositionDTO> debtPositions = debtPositionSearchService.getAllDebtPositionsByIngestionFlowFileId(ingestionFlowFileId);

        StringBuilder errors = new StringBuilder();
        debtPositions.forEach(debtPosition -> {
            try {
                // TODO invoke workflow sync and add any error (P4ADEV-2344)
                // TODO invoke workflow status (P4ADEV-2345)
            } catch (Exception e) {
                log.error("Error synchronizing debt position with id {}: {}", debtPosition.getDebtPositionId(), e.getMessage());
                errors.append("Error on debt position with iupdOrg " + debtPosition.getIupdOrg() + ": " + e.getMessage() + " \n");
            }
        });

        return errors.toString();
    }
}
