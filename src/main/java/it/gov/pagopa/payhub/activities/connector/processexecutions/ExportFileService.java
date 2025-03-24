package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
import java.util.Optional;

public interface ExportFileService {
    Optional<PaidExportFile> findPaidExportFileById(Long exportFileId);
    Optional<ExportFile> findById(Long exportFileId);
    Integer updateStatus(Long exportFileId, ExportFileStatus oldStatus, ExportFileStatus newStatus, String codError);
}
