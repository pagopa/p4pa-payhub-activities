package it.gov.pagopa.payhub.activities.activity.ingestionflow.orgsilservice;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.orgsilservice.OrgSilServiceProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Lazy
@Component
public class OrgSilServiceIngestionActivityImpl extends BaseIngestionFlowFileActivity<OrgSilServiceIngestionFlowFileResult> implements
        OrgSilServiceIngestionActivity {


    private final CsvService csvService;
    private final OrgSilServiceProcessingService orgSilServiceProcessingService;

    public OrgSilServiceIngestionActivityImpl(
            IngestionFlowFileService ingestionFlowFileService,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            FileArchiverService fileArchiverService,
            CsvService csvService,
            OrgSilServiceProcessingService orgSilServiceProcessingService) {
        super(ingestionFlowFileService, ingestionFlowFileRetrieverService,
                fileArchiverService);
        this.csvService = csvService;

        this.orgSilServiceProcessingService = orgSilServiceProcessingService;
    }

    @Override
    protected IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
        return IngestionFlowFileTypeEnum.ORGANIZATIONS_SIL_SERVICE;
    }

    @Override
    protected OrgSilServiceIngestionFlowFileResult handleRetrievedFiles(
            List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
        Path filePath = retrievedFiles.getFirst();
        Path workingDirectory = filePath.getParent();
        log.info("Processing file: {}", filePath);

        try {
            return csvService.readCsv(filePath,
                    OrgSilServiceIngestionFlowFileDTO.class, (csvIterator, readerException) ->
                            orgSilServiceProcessingService.processOrgSilService(csvIterator, readerException,
                                    ingestionFlowFileDTO, workingDirectory), null);
        } catch (Exception e) {
            log.error("Error processing file {}: {}", filePath, e.getMessage(), e);
            throw new InvalidIngestionFileException(String.format("Error processing file %s: %s", filePath, e.getMessage()));
        }

    }
}
