package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class IngestionFlowFileProcessingLockerActivityImpl implements IngestionFlowFileProcessingLockerActivity {
    private final IngestionFlowFileService ingestionFlowFileService;

    public IngestionFlowFileProcessingLockerActivityImpl(IngestionFlowFileService ingestionFlowFileService) {
        this.ingestionFlowFileService = ingestionFlowFileService;
    }

    @Override
    public boolean acquireIngestionFlowFileProcessingLock(Long ingestionFlowFileId) {
        log.info("Updating IngestionFlowFile {} to new status PROCESSING", ingestionFlowFileId);
        return ingestionFlowFileService.updateProcessingIfNoOtherProcessing(ingestionFlowFileId) == 1;
    }
}
