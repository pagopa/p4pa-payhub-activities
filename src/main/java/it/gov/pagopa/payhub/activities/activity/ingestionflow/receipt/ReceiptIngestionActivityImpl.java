package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

/**
 * Implementation of {@link ReceiptIngestionActivity} for processing Receipts ingestion files.
 * This class handles file retrieval, parsing, archiving, and deletion of Receipts files.
 */
@Slf4j
@Lazy
@Service
public class ReceiptIngestionActivityImpl extends BaseIngestionFlowFileActivity<ReceiptIngestionFlowFileResult> implements ReceiptIngestionActivity {

    private final CsvService csvService;
    private final ReceiptProcessingService receiptProcessingService;

    /**
     * Constructor to initialize dependencies for Receipts ingestion.
     *
     * @param ingestionFlowFileService          DAO for accessing ingestion flow file records.
     * @param ingestionFlowFileRetrieverService Service for retrieving and unzipping ingestion flow files.
     * @param fileArchiverService               Service for archiving files.
     * @param csvService                        Service for handling CSV file operations.
     * @param receiptProcessingService          Service for processing receipts.
     */
    public ReceiptIngestionActivityImpl(IngestionFlowFileService ingestionFlowFileService,
                                        IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
                                        FileArchiverService fileArchiverService,
                                        CsvService csvService,
                                        ReceiptProcessingService receiptProcessingService) {
        super(ingestionFlowFileService, ingestionFlowFileRetrieverService, fileArchiverService);
        this.csvService = csvService;
        this.receiptProcessingService = receiptProcessingService;
    }

    @Override
    protected IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
        return IngestionFlowFile.IngestionFlowFileTypeEnum.RECEIPT;
    }

    @Override
    protected ReceiptIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
        Path filePath = retrievedFiles.getFirst();
        Path workingDirectory = filePath.getParent();
        log.info("Processing file: {}", filePath);

        try {
            ReceiptIngestionFlowFileResult result = csvService.readCsv(filePath, ReceiptIngestionFlowFileDTO.class, (csvIterator, readerExceptions) ->
                    receiptProcessingService.processReceipts(csvIterator, readerExceptions, ingestionFlowFileDTO, workingDirectory), ingestionFlowFileDTO.getFileVersion());

            result.setOrganizationId(ingestionFlowFileDTO.getOrganizationId());
            return result;
        } catch (Exception e) {
            log.error("Error processing file {}: {}", filePath, e.getMessage(), e);
            throw new InvalidIngestionFileException(String.format("Error processing file %s: %s", filePath, e.getMessage()));
        }
    }
}
