package it.gov.pagopa.payhub.activities.activity.ingestionflow.assessments;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.assessments.AssessmentsProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Lazy
@Component
public class AssessmentsIngestionActivityImpl extends
        BaseIngestionFlowFileActivity<AssessmentsIngestionFlowFileResult> implements
        AssessmentsIngestionActivity {

    private final CsvService csvService;
    private final AssessmentsProcessingService assessmentsProcessingService;

    public AssessmentsIngestionActivityImpl(IngestionFlowFileService ingestionFlowFileService,
                                            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
                                            FileArchiverService fileArchiverService,
                                            CsvService csvService,
                                            AssessmentsProcessingService assessmentsProcessingService) {
        super(ingestionFlowFileService, ingestionFlowFileRetrieverService,
                fileArchiverService);
        this.csvService = csvService;
        this.assessmentsProcessingService = assessmentsProcessingService;
    }

    @Override
    protected IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
        return IngestionFlowFile.IngestionFlowFileTypeEnum.ASSESSMENTS;
    }

    @Override
    protected AssessmentsIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {

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
                    AssessmentsIngestionFlowFileDTO.class, (csvIterator, readerException) ->
                            assessmentsProcessingService.processAssessments(csvIterator,
                                    readerException,
                                    ingestionFlowFileDTO, workingDirectory), null);
        } catch (Exception e) {
            log.error("Error processing file {}: {}", filePath, e.getMessage(), e);
            throw new InvalidIngestionFileException(
                    String.format("Error processing file %s: %s", filePath, e.getMessage()));
        }
    }
}
