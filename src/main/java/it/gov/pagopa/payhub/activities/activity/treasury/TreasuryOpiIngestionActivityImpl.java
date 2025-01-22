package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpiParserService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link TreasuryOpiIngestionActivity} for processing OPI treasury ingestion files.
 * This class handles file retrieval, parsing, archiving, and deletion of OPI treasury files.
 */
@Slf4j
@Lazy
@Component
public class TreasuryOpiIngestionActivityImpl extends BaseIngestionFlowFileActivity<TreasuryIufResult> implements TreasuryOpiIngestionActivity {

    private final TreasuryOpiParserService treasuryOpiParserService;
    private final TreasuryErrorsArchiverService errorsArchiverService;

    /**
     * Constructor to initialize dependencies for OPI treasury ingestion.
     *
     * @param ingestionFlowFileService              DAO for accessing ingestion flow file records.
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
    protected TreasuryIufResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
        int ingestionFlowFilesRetrievedSize = retrievedFiles.size();

        List<TreasuryIufResult> treasuryIufResultList = retrievedFiles.stream()
            .map(path -> {
                try {
                    return treasuryOpiParserService.parseData(path, ingestionFlowFileDTO, ingestionFlowFilesRetrievedSize);
                } catch (Exception e) {
                    log.error("Error processing file {}: {}", path, e.getMessage());
                    return new TreasuryIufResult(Collections.emptyList(), Collections.emptyList(), ingestionFlowFileDTO.getOrganizationId(), false, e.getMessage(), null);
                }
            })
            .toList();

        String discardsFileName = errorsArchiverService.archiveErrorFiles(retrievedFiles.getFirst().getParent(), ingestionFlowFileDTO);

        boolean isSuccess = treasuryIufResultList.stream().allMatch(TreasuryIufResult::isSuccess);

        return new TreasuryIufResult(
            treasuryIufResultList.stream()
                .flatMap(result -> result.getIufs().stream())
                .distinct()
                .toList(),
            treasuryIufResultList.stream()
                .flatMap(result -> result.getTreasuryIds().stream())
                .distinct()
                .toList(),
            ingestionFlowFileDTO.getOrganizationId(),
            isSuccess,
            isSuccess ? null : "error occurred",
            discardsFileName
        );
    }

    @Override
    protected TreasuryIufResult onErrorResult(Exception e) {
        return new TreasuryIufResult(Collections.emptyList(), Collections.emptyList(), null, false, e.getMessage(), null);
    }

}
