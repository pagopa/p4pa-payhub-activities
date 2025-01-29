package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpiParserService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link TreasuryOpiIngestionActivity} for processing OPI treasury ingestion files.
 * This class handles file retrieval, parsing, archiving, and deletion of OPI treasury files.
 */
@Slf4j
@Lazy
@Component
public class TreasuryOpiIngestionActivityImpl extends BaseIngestionFlowFileActivity<TreasuryIufIngestionFlowFileResult> implements TreasuryOpiIngestionActivity {

    private final TreasuryOpiParserService treasuryOpiParserService;
    private final TreasuryErrorsArchiverService errorsArchiverService;

    /**
     * Constructor to initialize dependencies for OPI treasury ingestion.
     *
     * @param ingestionFlowFileService          DAO for accessing ingestion flow file records.
     * @param ingestionFlowFileRetrieverService Service for retrieving and unzipping ingestion flow files.
     * @param treasuryOpiParserService          Service for parsing treasury OPI files.
     * @param ingestionFlowFileArchiverService  Service for archiving files.
     */
    public TreasuryOpiIngestionActivityImpl(
            IngestionFlowFileService ingestionFlowFileService,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            TreasuryOpiParserService treasuryOpiParserService,
            IngestionFlowFileArchiverService ingestionFlowFileArchiverService, TreasuryErrorsArchiverService errorsArchiverService

    ) {
        super(ingestionFlowFileService, ingestionFlowFileRetrieverService, ingestionFlowFileArchiverService);
        this.treasuryOpiParserService = treasuryOpiParserService;
        this.errorsArchiverService = errorsArchiverService;
    }

    @Override
    protected IngestionFlowFile.FlowFileTypeEnum getHandledIngestionFlowFileType() {
        return IngestionFlowFile.FlowFileTypeEnum.TREASURY_OPI;
    }

    @Override
    protected TreasuryIufIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
        int ingestionFlowFilesRetrievedSize = retrievedFiles.size();

        Map<String, String> iuf2TreasuryIdMap = retrievedFiles.stream()
                .map(path -> {
                    try {
                        return treasuryOpiParserService.parseData(path, ingestionFlowFileDTO, ingestionFlowFilesRetrievedSize);
                    } catch (Exception e) {
                        log.error("Error processing file {}: {}", path, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .flatMap(m->m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        String discardsFileName = errorsArchiverService.archiveErrorFiles(retrievedFiles.getFirst().getParent(), ingestionFlowFileDTO);

        return new TreasuryIufIngestionFlowFileResult(
                iuf2TreasuryIdMap,
                ingestionFlowFileDTO.getOrganizationId(),
                discardsFileName!=null? "There were some errors during TreasuryOPI file ingestion. Please check error file": null,
                discardsFileName
        );
    }

}
