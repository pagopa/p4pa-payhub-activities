package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
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

    private static final List<String> DEFAULT_ORDERING = List.of("debtPositionId","asc");

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

            if(pagedDebtPositions == null){
                throw new InvalidValueException("No debt positions found for the ingestion flow file with id " + ingestionFlowFileId);
            }

            pagedDebtPositions.getContent().forEach(debtPosition -> {
                try {
                    // TODO invoke workflow sync and add any error (P4ADEV-2344)
                    // TODO invoke workflow status (P4ADEV-2345)
                } catch (Exception e) {
                    log.error("Error synchronizing debt position with id {}: {}", debtPosition.getDebtPositionId(), e.getMessage());
                    errors.append("Error on debt position with iupdOrg " + debtPosition.getIupdOrg() + ": " + e.getMessage() + " \n");
                }
            });

            currentPage++;
            hasMorePages = currentPage < pagedDebtPositions.getTotalPages();
        }

        return errors.toString();
    }
}
