package it.gov.pagopa.payhub.activities.activity.ingestionflow.assessmentsregistry;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.assessmentsregistry.AssessmentsRegistryProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Lazy
@Component
public class AssessmentsRegistryIngestionActivityImpl extends
        BaseIngestionFlowFileActivity<AssessmentsRegistryIngestionFlowFileResult> implements
        AssessmentsRegistryIngestionActivity {

    private final CsvService csvService;
    private final AssessmentsRegistryProcessingService assessmentsRegistryProcessingService;

    public AssessmentsRegistryIngestionActivityImpl(IngestionFlowFileService ingestionFlowFileService,
                                                    IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
                                                    FileArchiverService fileArchiverService,
                                                    CsvService csvService,
                                                    AssessmentsRegistryProcessingService assessmentsRegistryProcessingService) {
        super(ingestionFlowFileService, ingestionFlowFileRetrieverService,
                fileArchiverService);
        this.csvService = csvService;
        this.assessmentsRegistryProcessingService = assessmentsRegistryProcessingService;
    }

    @Override
    protected IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
        return IngestionFlowFile.IngestionFlowFileTypeEnum.ASSESSMENTS_REGISTRY;
    }

    @Override
    protected AssessmentsRegistryIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {

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
            AssessmentsRegistryIngestionFlowFileResult result = new AssessmentsRegistryIngestionFlowFileResult();
            return csvService.readCsv(filePath,
                    AssessmentsRegistryIngestionFlowFileDTO.class, (csvIterator, readerException) ->
                            assessmentsRegistryProcessingService.processAssessmentsRegistry(csvIterator,
                                    readerException, ingestionFlowFileDTO, workingDirectory, result),
                    result,
                    ingestionFlowFileDTO.getFileVersion());
        } catch (Exception e) {
            log.error("Error processing file {} with version {}: {}", filePath, ingestionFlowFileDTO.getFileVersion(), e.getMessage(), e);
            throw new InvalidIngestionFileException(String.format("Error processing file %s with version %s: %s", filePath, ingestionFlowFileDTO.getFileVersion(), e.getMessage()));
        }
    }
}
