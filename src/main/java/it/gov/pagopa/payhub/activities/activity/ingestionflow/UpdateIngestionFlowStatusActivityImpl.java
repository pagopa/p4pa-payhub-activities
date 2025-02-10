package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Implementation for the activity to update the ingestion flow status
 */

@Slf4j
@Component
@Lazy
public class UpdateIngestionFlowStatusActivityImpl implements UpdateIngestionFlowStatusActivity {
    private final IngestionFlowFileService ingestionFlowFileService;

    public UpdateIngestionFlowStatusActivityImpl(IngestionFlowFileService ingestionFlowFileService) {
        this.ingestionFlowFileService = ingestionFlowFileService;
    }

    @Override
    public void updateStatus(Long id, IngestionFlowFile.StatusEnum  oldStatus, IngestionFlowFile.StatusEnum newStatus, String codError, String discardFileName) {
        log.info("Updating IngestionFlowFile {} to new status {} from {}", id, newStatus, oldStatus);
        if(ingestionFlowFileService.updateStatus(id, oldStatus, newStatus, codError, discardFileName) != 1){
            throw new IngestionFlowFileNotFoundException("Cannot update ingestionFlowFile having id " + ingestionFlowFileService + " to status " + newStatus);
        }
    }
}
