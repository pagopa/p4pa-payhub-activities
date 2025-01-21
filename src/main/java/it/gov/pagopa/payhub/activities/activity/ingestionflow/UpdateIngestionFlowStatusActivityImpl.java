package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public boolean updateStatus(Long id, String newStatus, String codError, String discardFileName) {
        log.info("Updating IngestionFlowFile {} to new status {}", id, newStatus);
        if(id==null){
            throw new IllegalArgumentException("A null IngestionFlowFile was provided when updating its status to " + newStatus);
        }
        if(StringUtils.isBlank(newStatus)){
            throw new IllegalArgumentException("A null IngestionFlowFile status was provided when updating the id " + id);
        }
        return ingestionFlowFileService.updateStatus(id, newStatus, codError, discardFileName) == 1;
    }
}
