package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryOpiInvalidFileException;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Lazy
@Service
@Slf4j
public class TreasuryOpiParserService {


    private final List<TreasuryVersionHandlerService> versionHandlerServices;

    private final TreasuryService treasuryService;

    public TreasuryOpiParserService(List<TreasuryVersionHandlerService> versionHandlerServices,
                                    TreasuryService treasuryService) {
        this.versionHandlerServices = versionHandlerServices;
        this.treasuryService = treasuryService;
    }

    public TreasuryIufResult parseData(Path treasuryOpiFilePath, IngestionFlowFile ingestionFlowFileDTO, int totalNumberOfTreasuryOpiFiles) {
        File ingestionFlowFile = treasuryOpiFilePath.toFile();

        List<Treasury> newTreasuries = versionHandlerServices.stream()
                .map(m -> m.handle(ingestionFlowFile, ingestionFlowFileDTO, totalNumberOfTreasuryOpiFiles))
                .filter(map -> !map.isEmpty())
                .findFirst()
                .orElseThrow(() -> new TreasuryOpiInvalidFileException("Cannot parse treasury Opi file " + ingestionFlowFile));

        Map<String, String> iufTreasuryIdMap = newTreasuries.stream()
            .collect(Collectors.toMap(
                Treasury::getIuf,
                treasury -> treasuryService.insert(treasury)
                    .orElseThrow(() -> new TreasuryOpiInvalidFileException("Cannot insert treasury " + treasury))
                    .getTreasuryId()
            ));

        return new TreasuryIufResult(iufTreasuryIdMap, ingestionFlowFileDTO.getOrganizationId(), true, null, null);
    }
}
