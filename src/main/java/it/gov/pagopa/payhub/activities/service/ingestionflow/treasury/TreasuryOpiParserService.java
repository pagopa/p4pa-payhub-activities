package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryOpiInvalidFileException;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.gov.pagopa.payhub.activities.util.TreasuryUtils.generateTechnicalIuf;

@Lazy
@Service
@Slf4j
public class TreasuryOpiParserService {

    private final List<TreasuryVersionHandlerService> versionHandlerServices;
    private final TreasuryService treasuryService;
    private final TreasuryErrorsArchiverService treasuryErrorsArchiverService;

    public TreasuryOpiParserService(List<TreasuryVersionHandlerService> versionHandlerServices,
                                    TreasuryService treasuryService, TreasuryErrorsArchiverService treasuryErrorsArchiverService) {
        this.versionHandlerServices = versionHandlerServices;
        this.treasuryService = treasuryService;
        this.treasuryErrorsArchiverService = treasuryErrorsArchiverService;
    }

    public Pair<IngestionFlowFileResult, Map<String, String>> parseData(Path treasuryOpiFilePath, IngestionFlowFile ingestionFlowFileDTO, int totalNumberOfTreasuryOpiFiles) {
        File ingestionFlowFile = treasuryOpiFilePath.toFile();

        List<TreasuryErrorDTO> parsingErrors = new ArrayList<>();

        Pair<IngestionFlowFileResult, List<Treasury>> newTreasuries = versionHandlerServices.stream()
                .map(m -> m.handle(ingestionFlowFile, ingestionFlowFileDTO, totalNumberOfTreasuryOpiFiles, parsingErrors))
                .filter(r -> r.getRight() != null)
                .findFirst()
                .orElseGet(() -> {
                    List<TreasuryErrorDTO> uniqueErrors = parsingErrors.stream()
                            .distinct()
                            .toList();

                    treasuryErrorsArchiverService.writeErrors(
                            ingestionFlowFile.toPath().getParent(),
                            ingestionFlowFileDTO,
                            uniqueErrors, null);

                    throw new TreasuryOpiInvalidFileException("Cannot parse treasury Opi file " + ingestionFlowFile);
                });

        return Pair.of(
                newTreasuries.getLeft(),
                newTreasuries.getRight().stream()
                        .map(treasury -> {
                            String treasuryId = Objects.requireNonNull(treasuryService.insert(treasury).getTreasuryId());
                            return Map.entry(
                                    treasury.getIuf() == null ? generateTechnicalIuf(treasuryId) : treasury.getIuf(),
                                    treasuryId
                            );
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    }
}
