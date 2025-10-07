package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtpositiontypeorg;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontypeorg.DebtPositionTypeOrgProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Lazy
@Component
public class DebtPositionTypeOrgIngestionActivityImpl extends
        BaseIngestionFlowFileActivity<DebtPositionTypeOrgIngestionFlowFileResult> implements
        DebtPositionTypeOrgIngestionActivity {

    private final CsvService csvService;
    private final DebtPositionTypeOrgProcessingService debtPositionTypeOrgProcessingService;

    public DebtPositionTypeOrgIngestionActivityImpl(
            IngestionFlowFileService ingestionFlowFileService,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            FileArchiverService fileArchiverService,
            CsvService csvService,
            DebtPositionTypeOrgProcessingService debtPositionTypeOrgProcessingService) {
        super(ingestionFlowFileService, ingestionFlowFileRetrieverService, fileArchiverService);
        this.csvService = csvService;
        this.debtPositionTypeOrgProcessingService = debtPositionTypeOrgProcessingService;
    }

    @Override
    protected IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
        return IngestionFlowFile.IngestionFlowFileTypeEnum.DEBT_POSITIONS_TYPE_ORG;
    }

    @Override
    protected DebtPositionTypeOrgIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {

        Path filePath = retrievedFiles.getFirst();
        Path workingDirectory = filePath.getParent();
        log.info("Processing file: {}", filePath);

        try {
            return csvService.readCsv(filePath,
                    DebtPositionTypeOrgIngestionFlowFileDTO.class, (csvIterator, readerException) ->
                            debtPositionTypeOrgProcessingService.processDebtPositionTypeOrg(csvIterator,
                                    readerException,
                                    ingestionFlowFileDTO, workingDirectory), ingestionFlowFileDTO.getFileVersion());
        } catch (Exception e) {
            log.error("Error processing file {} with version {}: {}", filePath, ingestionFlowFileDTO.getFileVersion(), e.getMessage(), e);
            throw new InvalidIngestionFileException(String.format("Error processing file %s with version %s: %s", filePath, ingestionFlowFileDTO.getFileVersion(), e.getMessage()));
        }
    }
}
