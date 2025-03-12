package it.gov.pagopa.payhub.activities.connector.processexecutions;


import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.ExportFileClient;
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
    public Optional<PaidExportFile> findById(Long exportFileId) {
        log.info("Finding a PaidExportFile with id {}", exportFileId);
        return  Optional.ofNullable(exportFileClient.findById(exportFileId, authnService.getAccessToken()));
    }
}
