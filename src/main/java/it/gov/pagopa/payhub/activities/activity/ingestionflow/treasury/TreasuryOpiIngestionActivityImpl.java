package it.gov.pagopa.payhub.activities.activity.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryOpiParserService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
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
     * @param fileArchiverService  Service for archiving files.
     */
    public TreasuryOpiIngestionActivityImpl(
            IngestionFlowFileService ingestionFlowFileService,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            TreasuryOpiParserService treasuryOpiParserService,
            FileArchiverService fileArchiverService, TreasuryErrorsArchiverService errorsArchiverService

    ) {
        super(ingestionFlowFileService, ingestionFlowFileRetrieverService, fileArchiverService);
        this.treasuryOpiParserService = treasuryOpiParserService;
        this.errorsArchiverService = errorsArchiverService;
    }

    @Override
    protected IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
        return IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_OPI;
    }

    @Override
    protected TreasuryIufIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
        int ingestionFlowFilesRetrievedSize = retrievedFiles.size();
        final List<String> unsuccessfulParsedFiles = new ArrayList<>();

        long[] totalRows = {0L};
        long[] processedRows = {0L};
        String[] fileVersion = {""};

        Map<String, String> iuf2TreasuryIdMap = retrievedFiles.stream()
                .map(path -> {
                    try {
                        return treasuryOpiParserService.parseData(path, ingestionFlowFileDTO, ingestionFlowFilesRetrievedSize);
                    } catch (Exception e) {
                        log.error("Error processing file {}: {}", path, e.getMessage(), e);
                        unsuccessfulParsedFiles.add(path.getFileName() + ":" + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .flatMap(r -> {
                    fileVersion[0] = r.getLeft().getFileVersion();
                    totalRows[0] += r.getLeft().getTotalRows();
                    processedRows[0] += r.getLeft().getProcessedRows();
                    return r.getRight().entrySet().stream();
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        String discardsFileName = errorsArchiverService.archiveErrorFiles(retrievedFiles.getFirst().getParent(), ingestionFlowFileDTO);
        String errorDescription = buildErrorDescription(unsuccessfulParsedFiles, discardsFileName);

        return TreasuryIufIngestionFlowFileResult.builder()
                .iuf2TreasuryIdMap(iuf2TreasuryIdMap)
                .fileVersion(fileVersion[0])
                .totalRows(totalRows[0])
                .processedRows(processedRows[0])
                .organizationId(ingestionFlowFileDTO.getOrganizationId())
                .errorDescription(errorDescription)
                .discardedFileName(discardsFileName)
                .build();
    }

    private static String buildErrorDescription(List<String> unsuccessfulParsedFiles, String discardsFileName) {
        String errorDescription = null;
        if (!unsuccessfulParsedFiles.isEmpty() || discardsFileName !=null) {
            errorDescription = "There were some errors during TreasuryOPI file ingestion.";
            if(discardsFileName !=null){
                errorDescription += " Please check error file.";
            }
            if(!unsuccessfulParsedFiles.isEmpty()){
                errorDescription += "\n" + String.join("\n", unsuccessfulParsedFiles);
            }
        }
        return errorDescription;
    }

}
