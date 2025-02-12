package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class IngestionFlowFileLockerActivityImpl implements IngestionFlowFileLockerActivity {
    private final IngestionFlowFileService ingestionFlowFileService;

    public IngestionFlowFileLockerActivityImpl(IngestionFlowFileService ingestionFlowFileService) {
        this.ingestionFlowFileService = ingestionFlowFileService;
    }

    @Override
    public void updateProcessingIfNoOtherProcessing(Long ingestionFlowFileId) {
        log.info("Updating IngestionFlowFile {} to new status PROCESSING", ingestionFlowFileId);
        if (ingestionFlowFileService.updateProcessingIfNoOtherProcessing(ingestionFlowFileId) != 1) {
            throw new IngestionFlowFileNotFoundException("Cannot update ingestionFlowFile having id " + ingestionFlowFileService + " to status " + IngestionFlowFile.StatusEnum.PROCESSING);
        }
    }
}
