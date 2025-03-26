package it.gov.pagopa.payhub.activities.connector.processexecutions;


import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.ExportFileClient;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Lazy
@Service
@Slf4j
public class ExportFileServiceImpl implements ExportFileService{
    private final ExportFileClient exportFileClient;
    private final AuthnService authnService;

    public ExportFileServiceImpl(ExportFileClient exportFileClient, AuthnService authnService) {
        this.exportFileClient = exportFileClient;
        this.authnService = authnService;
    }

    @Override
    public Optional<PaidExportFile> findPaidExportFileById(Long exportFileId) {
        log.info("Finding a PaidExportFile with id {}", exportFileId);
        return  Optional.ofNullable(exportFileClient.findPaidExportFileById(exportFileId, authnService.getAccessToken()));
    }

    @Override
    public Optional<ExportFile> findById(Long exportFileId) {
        log.info("Finding an ExportFile with id {}", exportFileId);
        return Optional.ofNullable(exportFileClient.findById(exportFileId, authnService.getAccessToken()));
    }

    @Override
    public Integer updateStatus(Long exportFileId, ExportFileStatus oldStatus, ExportFileStatus newStatus, String codError) {
        return exportFileClient.updateStatus(exportFileId, oldStatus, newStatus, codError, authnService.getAccessToken());
    }
}
