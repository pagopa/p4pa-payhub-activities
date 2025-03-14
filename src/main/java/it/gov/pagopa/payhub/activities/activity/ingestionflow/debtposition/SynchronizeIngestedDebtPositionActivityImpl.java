package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedDebtPositions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Lazy
@Service
public class SynchronizeIngestedDebtPositionActivityImpl implements SynchronizeIngestedDebtPositionActivity {

    private final DebtPositionService debtPositionService;
    private final Integer pageSize;

    private static final List<String> DEFAULT_ORDERING = List.of("debtPositionId,asc");

    public SynchronizeIngestedDebtPositionActivityImpl(DebtPositionService debtPositionService,
                                                       @Value("${query-limits.debt-positions.size}") Integer pageSize) {
        this.debtPositionService = debtPositionService;
        this.pageSize = pageSize;
    }

    @Override
    public String synchronizeIngestedDebtPosition(Long ingestionFlowFileId) {
        log.info("Synchronizing all debt positions related to ingestion flow file id {}", ingestionFlowFileId);

        StringBuilder errors = new StringBuilder();

        int currentPage = 0;
        boolean hasMorePages = true;

        while (hasMorePages) {
            PagedDebtPositions pagedDebtPositions = debtPositionService.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId,
                    currentPage,
                    pageSize,
                    DEFAULT_ORDERING);

            log.info("Synchronizing page {} of {} retrieved searching debt positions related to ingestionFlowFileId {} (totalElements {})",
                    currentPage, pagedDebtPositions.getTotalPages(), pagedDebtPositions.getTotalElements(), ingestionFlowFileId);

            pagedDebtPositions.getContent().forEach(debtPosition -> {
                try {
                    // TODO invoke workflow sync and add any error (P4ADEV-2344)
                    // TODO invoke workflow status (P4ADEV-2345)
                } catch (Exception e) {
                    log.error("Error synchronizing debt position with id {} and iupdOrg {}: {}", debtPosition.getDebtPositionId(), debtPosition.getIupdOrg(), e.getMessage());
                    errors.append("Error on debt position with iupdOrg " + debtPosition.getIupdOrg() + ": " + e.getMessage() + " \n");
                }
            });

            currentPage++;
            hasMorePages = currentPage < pagedDebtPositions.getTotalPages();
        }

        log.info("Synchronization of all debt positions related to ingestion flow file id {} completed", ingestionFlowFileId);
        return errors.toString();
    }
}
