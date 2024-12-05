package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation for the activity to update the ingestion flow status
 */

@Slf4j
@Component
public class UpdateIngestionFlowStatusActivityImpl implements UpdateIngestionFlowStatusActivity {
    private final IngestionFlowFileDao ingestionFlowFileDao;

    public UpdateIngestionFlowStatusActivityImpl(IngestionFlowFileDao ingestionFlowFileDao) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
    }

    @Override
    public Optional<Boolean> updateStatus(String id, String newStatus) {
        Long ingestionFlowId = Long.valueOf(id);
        return ingestionFlowFileDao.updateStatus(ingestionFlowId, newStatus);
    }
}
