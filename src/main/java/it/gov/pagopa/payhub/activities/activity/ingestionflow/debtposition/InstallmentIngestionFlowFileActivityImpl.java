package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.CsvReadResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition.InstallmentProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

/**
 * Implementation of {@link InstallmentIngestionFlowFileActivity} for processing Installments ingestion files.
 * This class handles file retrieval, parsing, archiving, and deletion of Installments files.
 */
@Slf4j
@Lazy
@Service
public class InstallmentIngestionFlowFileActivityImpl extends BaseIngestionFlowFileActivity<InstallmentIngestionFlowFileResult> implements InstallmentIngestionFlowFileActivity {

    private final CsvService csvService;
    private final InstallmentProcessingService installmentProcessingService;

    /**
     * Constructor to initialize dependencies for Installments ingestion.
     *
     * @param ingestionFlowFileService          DAO for accessing ingestion flow file records.
     * @param ingestionFlowFileRetrieverService Service for retrieving and unzipping ingestion flow files.
     * @param ingestionFlowFileArchiverService  Service for archiving files.
     * @param csvService                        Service for handling CSV file operations.
     * @param installmentProcessingService      Service for processing installments.
     */
    public InstallmentIngestionFlowFileActivityImpl(IngestionFlowFileService ingestionFlowFileService,
                                                    IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
                                                    IngestionFlowFileArchiverService ingestionFlowFileArchiverService,
                                                    CsvService csvService,
                                                    InstallmentProcessingService installmentProcessingService) {
        super(ingestionFlowFileService, ingestionFlowFileRetrieverService, ingestionFlowFileArchiverService);
        this.csvService = csvService;
        this.installmentProcessingService = installmentProcessingService;
    }

    @Override
    protected IngestionFlowFile.FlowFileTypeEnum getHandledIngestionFlowFileType() {
        return IngestionFlowFile.FlowFileTypeEnum.DP_INSTALLMENTS;
    }

    @Override
    protected InstallmentIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
        Path filePath = retrievedFiles.getFirst();
        Path workingDirectory = filePath.getParent();
        log.info("Processing file: {}", filePath);

        try {
            CsvReadResult<InstallmentIngestionFlowFileDTO> csvReadResult =
                    csvService.readCsv(filePath, InstallmentIngestionFlowFileDTO.class, InstallmentIngestionFlowFileDTO::setIngestionFlowFileLineNumber);

            return installmentProcessingService
                    .processInstallments(csvReadResult.getDataStream(), ingestionFlowFileDTO, workingDirectory, csvReadResult.getTotalRows());
        } catch (Exception e) {
            log.error("Error processing file {}: {}", filePath, e.getMessage());
            throw new InvalidIngestionFileException(String.format("Error processing file %s: %s", filePath, e.getMessage()));
        }
    }
}
