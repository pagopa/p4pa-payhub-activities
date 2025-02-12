package it.gov.pagopa.payhub.activities.activity.ingestionflow.massive;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.massive.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

/**
 * Implementation of {@link InstallmentIngestionFlowFileActivity} for processing Installments ingestion files.
 * This class handles file retrieval, parsing, archiving, and deletion of Installments files.
 */
@Slf4j
@Lazy
@Component
public class InstallmentIngestionFlowFileActivityImpl extends BaseIngestionFlowFileActivity<InstallmentIngestionFlowFileResult> implements InstallmentIngestionFlowFileActivity {

    /**
     * Constructor to initialize dependencies for Installments ingestion.
     *
     * @param ingestionFlowFileService          DAO for accessing ingestion flow file records.
     * @param ingestionFlowFileRetrieverService Service for retrieving and unzipping ingestion flow files.
     * @param ingestionFlowFileArchiverService  Service for archiving files.
     */
    public InstallmentIngestionFlowFileActivityImpl(IngestionFlowFileService ingestionFlowFileService,
                                                    IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
                                                    IngestionFlowFileArchiverService ingestionFlowFileArchiverService) {
        super(ingestionFlowFileService, ingestionFlowFileRetrieverService, ingestionFlowFileArchiverService);
    }

    @Override
    protected IngestionFlowFile.FlowFileTypeEnum getHandledIngestionFlowFileType() {
        return null; // TODO to be implemented with task https://pagopa.atlassian.net/browse/P4ADEV-2126
    }

    @Override
    protected InstallmentIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
        return null; // TODO to be implemented with task https://pagopa.atlassian.net/browse/P4ADEV-2126
    }
}
