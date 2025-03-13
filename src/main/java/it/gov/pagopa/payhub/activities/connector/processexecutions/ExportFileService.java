package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;

import java.util.Optional;

public interface ExportFileService {
    Optional<PaidExportFile> findById(Long exportFileId);
}
