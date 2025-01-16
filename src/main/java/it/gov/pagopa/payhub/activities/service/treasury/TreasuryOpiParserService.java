package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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

    public TreasuryIufResult parseData(Path treasuryOpiFilePath, IngestionFlowFileDTO ingestionFlowFileDTO, int totalNumberOfTreasuryOpiFiles) {
        File ingestionFlowFile = treasuryOpiFilePath.toFile();

        Map<TreasuryOperationEnum, List<Treasury>> op2TreasuriesMap = versionHandlerServices.stream()
                .map(m -> m.handle(ingestionFlowFile, ingestionFlowFileDTO, totalNumberOfTreasuryOpiFiles))
                .filter(map -> !map.isEmpty())
                .findFirst()
                .orElseThrow(() -> new TreasuryOpiInvalidFileException("Cannot parse treasury Opi file " + ingestionFlowFile));

        List<Treasury> newTreasuries = op2TreasuriesMap.get(TreasuryOperationEnum.INSERT);
        List<String> iufList = newTreasuries.stream()
            .map(treasuryDTO -> {
                treasuryService.insert(treasuryDTO);
                return treasuryDTO.getIuf();
              })
              .distinct()
              .toList();

        List<Treasury> deleteTreasuries = op2TreasuriesMap.get(TreasuryOperationEnum.DELETE);
        for (Treasury treasuryDTO : deleteTreasuries) {
                treasuryService.deleteByOrganizationIdAndBillCodeAndBillYear(
                        treasuryDTO.getOrganizationId(),
                        treasuryDTO.getBillCode(),
                        treasuryDTO.getBillYear());
        }
        return new TreasuryIufResult(iufList, true, null, null);
    }

}
