package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.payhub.activities.dto.exportflow.UpdateStatusRequest;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ReceiptsArchivingExportFile;

import java.util.Optional;

public interface ExportFileService {
    Optional<PaidExportFile> findPaidExportFileById(Long exportFileId);
    Optional<ReceiptsArchivingExportFile> findReceiptsArchivingExportFileById(Long exportFileId);
    Optional<ExportFile> findById(Long exportFileId);
    Integer updateStatus(UpdateStatusRequest updateStatusRequest);
}
