package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.IngestionFlowFileClient;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.PagedModelIngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Lazy
@Service
@Slf4j
public class IngestionFlowFileServiceImpl implements IngestionFlowFileService {

    private final IngestionFlowFileClient ingestionFlowFileClient;
    private final AuthnService authnService;

    public IngestionFlowFileServiceImpl(IngestionFlowFileClient ingestionFlowFileClient, AuthnService authnService) {
        this.ingestionFlowFileClient = ingestionFlowFileClient;
        this.authnService = authnService;
    }

    @Override
    public Optional<IngestionFlowFile> findById(Long ingestionFlowFileId) {
        return Optional.ofNullable(
                ingestionFlowFileClient.findById(ingestionFlowFileId, authnService.getAccessToken())
        );
    }

    @Override
    public Integer updateStatus(Long ingestionFlowFileId, IngestionFlowFileStatus oldStatus, IngestionFlowFileStatus newStatus, IngestionFlowFileResult ingestionFlowFileResult) {
        return ingestionFlowFileClient.updateStatus(ingestionFlowFileId, oldStatus, newStatus, ingestionFlowFileResult, authnService.getAccessToken());
    }

    @Override
    public List<IngestionFlowFile> findByOrganizationIdFlowTypeCreateDate(Long organizationId, IngestionFlowFileTypeEnum flowFileType, OffsetDateTime creationDateFrom) {
        log.info("Fetching IngestionFlowFile type {} by organizationId: {}, created from: {}", flowFileType, organizationId, creationDateFrom);
        PagedModelIngestionFlowFile pagedModelIngestionFlowFile = ingestionFlowFileClient
                .findByOrganizationIDFlowTypeCreateDate(organizationId, flowFileType, creationDateFrom, authnService.getAccessToken());
        return Objects.requireNonNull(pagedModelIngestionFlowFile.getEmbedded()).getIngestionFlowFiles();
    }

    @Override
    public List<IngestionFlowFile> findByOrganizationIdFlowTypeFilename(Long organizationId, IngestionFlowFileTypeEnum flowFileType, String fileName) {
        log.info("Fetching IngestionFlowFile type {} by organizationId: {} and file name: {}", flowFileType, organizationId, fileName);
        PagedModelIngestionFlowFile pagedModelIngestionFlowFile = ingestionFlowFileClient
                .findByOrganizationIDFlowTypeFilename(organizationId, flowFileType, fileName, authnService.getAccessToken());
        return Objects.requireNonNull(pagedModelIngestionFlowFile.getEmbedded()).getIngestionFlowFiles();
    }

    @Override
    public Integer updateProcessingIfNoOtherProcessing(Long ingestionFlowFileId) {
        return ingestionFlowFileClient.updateProcessingIfNoOtherProcessing(ingestionFlowFileId, authnService.getAccessToken());
    }

    @Override
    public Integer updatePdfGenerated(Long ingestionFlowFileId, Long pdfGenerated, String pdfGeneratedId) {
        return ingestionFlowFileClient.updatePdfGenerated(ingestionFlowFileId, pdfGenerated, pdfGeneratedId, authnService.getAccessToken());
    }
}
