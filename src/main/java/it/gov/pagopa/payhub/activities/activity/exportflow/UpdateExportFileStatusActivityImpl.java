package it.gov.pagopa.payhub.activities.activity.exportflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Implementation for the activity to update the export file status
 */

@Slf4j
@Component
@Lazy
public class UpdateExportFileStatusActivityImpl implements UpdateExportFileStatusActivity {
    private final ExportFileService exportFileService;

    public UpdateExportFileStatusActivityImpl(ExportFileService exportFileService) {
        this.exportFileService = exportFileService;
    }

    @Override
    public void updateStatus(Long exportFileId, ExportFileStatus oldStatus, ExportFileStatus newStatus) {
        log.info("Updating ExportFile {} to new status {} from {}", exportFileId, newStatus, oldStatus);
        if(exportFileService.updateStatus(exportFileId, oldStatus, newStatus, null) != 1){
            throw new ExportFileNotFoundException("Cannot update ExportFile having id " + exportFileId
                + " to status " + newStatus + " from status " + oldStatus);
        }
    }
}
