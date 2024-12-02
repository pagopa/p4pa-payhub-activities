package it.gov.pagopa.payhub.activities.paymentsreporting;

import it.gov.pagopa.payhub.activities.activity.fdr.UpdateIngestionFlowStatusActivity;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowDao;
import it.gov.pagopa.payhub.activities.paymentsreporting.service.IngestionFlowRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation for the activity to update the ingestion flow status
 */

@Slf4j
@Component
public class UpdateIngestionFlowStatusActivityImpl implements UpdateIngestionFlowStatusActivity {
    private final IngestionFlowRetrieverService ingestionFlowRetrieverService;

    public UpdateIngestionFlowStatusActivityImpl(IngestionFlowDao ingestionFlowDao) {
        ingestionFlowRetrieverService = new IngestionFlowRetrieverService(ingestionFlowDao);
    }
    /**
     * Updates the status of a IngestionFlow record identified by the provided ID.
     *
     * @param id        the unique identifier of the record to update.
     * @param newStatus the new status to set.
     * @return true if the update was successful, false otherwise.
     */
    @Override
    public boolean updateStatus(String id, String newStatus) {
        Long ingestionFlowId = Long.valueOf(id);
        try {
            return ingestionFlowRetrieverService.updateIngestionFlow(ingestionFlowId, newStatus);
        } catch (Exception e) {
            log.error("Error during update ingestion flow status - ingestionFlowId {} due to: {}", ingestionFlowId, e.getMessage());
        }
        return false;
    }

}
