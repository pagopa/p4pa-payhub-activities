package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.IngestionFlowFileClient;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.FlowFileTypeEnum;
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
    public Integer updateStatus(Long ingestionFlowFileId, IngestionFlowFile.StatusEnum status, String codError,String discardFileName) {
        return ingestionFlowFileClient.updateStatus(ingestionFlowFileId,status, codError,discardFileName, authnService.getAccessToken());

    }

    @Override
    public List<IngestionFlowFile> findByOrganizationIdFlowTypeCreateDate(Long organizationId, FlowFileTypeEnum flowFileType, OffsetDateTime creationDateFrom, OffsetDateTime creationDateTo) {
        PagedModelIngestionFlowFile pagedModelIngestionFlowFile = ingestionFlowFileClient
            .findByOrganizationIDFlowTypeCreateDate(organizationId, flowFileType, creationDateFrom, creationDateTo, authnService.getAccessToken());
        return Objects.requireNonNull(pagedModelIngestionFlowFile.getEmbedded()).getIngestionFlowFiles();
    }
}
