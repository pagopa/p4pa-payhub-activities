package it.gov.pagopa.payhub.activities.activity.ingestionflow.treasury.csv;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csv.TreasuryCsvProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Lazy
@Component
public class TreasuryCsvIngestionActivityImpl
        extends BaseIngestionFlowFileActivity<TreasuryIufIngestionFlowFileResult>
        implements TreasuryCsvIngestionActivity {

    private final CsvService csvService;
    private final TreasuryCsvProcessingService treasuryCsvProcessingService;

    protected TreasuryCsvIngestionActivityImpl(
            IngestionFlowFileService ingestionFlowFileService,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            FileArchiverService fileArchiverService,
            CsvService csvService,
            TreasuryCsvProcessingService treasuryCsvProcessingService) {

        super(ingestionFlowFileService, ingestionFlowFileRetrieverService, fileArchiverService);

        this.csvService = csvService;
        this.treasuryCsvProcessingService = treasuryCsvProcessingService;
    }

    @Override
    protected IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
        return IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_CSV;
    }

    @Override
    protected TreasuryIufIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {

        if (retrievedFiles.size() > 1) {
            String msg = String.format(
                    "Multiple files [%s] found for ingestion flow file ID %s. Only the first file will be processed.",
                    retrievedFiles.size(), ingestionFlowFileDTO.getIngestionFlowFileId());
            log.error(msg);
            throw new InvalidIngestionFileException(msg);
        }

        Path filePath = retrievedFiles.getFirst();
        Path workingDirectory = filePath.getParent();
        log.info("Processing file: {}", filePath);

        try {
            return csvService.readCsv(filePath,
                    TreasuryCsvIngestionFlowFileDTO.class,
                    (csvIterator, readerException) ->
                            treasuryCsvProcessingService.processTreasuryCsv(csvIterator, readerException, ingestionFlowFileDTO, workingDirectory),
                    null);

        } catch (Exception e) {
            log.error("Error processing file {}: {}", filePath, e.getMessage(), e);
            throw new InvalidIngestionFileException(
                    String.format("Error processing file %s: %s", filePath, e.getMessage()));
        }
    }
}
