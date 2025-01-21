package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

        List<String> iufs = new ArrayList<>();
        List<String> treasuryIds = new ArrayList<>();

        for (Treasury treasury : newTreasuries) {
            treasuryService.insert(treasury).ifPresentOrElse(t -> {
                iufs.add(t.getIuf());
                treasuryIds.add(t.getTreasuryId());
            }, () -> new TreasuryOpiInvalidFileException("Cannot insert treasury " + treasury));
        }

        return new TreasuryIufResult(iufs, treasuryIds, ingestionFlowFileDTO.getOrganizationId(), true, null, null);
    }
}
