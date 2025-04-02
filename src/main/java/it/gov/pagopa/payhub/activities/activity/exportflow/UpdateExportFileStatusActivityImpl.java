package it.gov.pagopa.payhub.activities.activity.exportflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.UpdateStatusRequest;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
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
    public void updateStatus(UpdateStatusRequest updateStatusRequest) {
        log.info("Updating ExportFile {} to new status {} from {}", updateStatusRequest.getExportFileId(), updateStatusRequest.getNewStatus(), updateStatusRequest.getOldStatus());
        if(exportFileService.updateStatus(updateStatusRequest) != 1){
            throw new ExportFileNotFoundException("Cannot update ExportFile having id " + updateStatusRequest.getExportFileId()
                + " to status " + updateStatusRequest.getNewStatus() + " from status " + updateStatusRequest.getOldStatus());
        }
    }
}
