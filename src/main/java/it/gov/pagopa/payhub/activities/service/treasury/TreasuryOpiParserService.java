package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@Lazy
@Service
@Slf4j
public class TreasuryOpiParserService {


    private final List<TreasuryVersionHandlerService> versionHandlerServices;
    private final TreasuryDao treasuryDao;

    public TreasuryOpiParserService(List<TreasuryVersionHandlerService> versionHandlerServices,
                                    TreasuryDao treasuryDao) {
        this.versionHandlerServices = versionHandlerServices;
        this.treasuryDao = treasuryDao;
    }

    public TreasuryIufResult parseData(Path treasuryOpiFilePath, IngestionFlowFileDTO ingestionFlowFileDTO, int totalNumberOfTreasuryOpiFiles) {
        File ingestionFlowFile = treasuryOpiFilePath.toFile();
        Set<String> iufList = new HashSet<>();

        Map<TreasuryOperationEnum, List<TreasuryDTO>> op2TreasuriesMap = versionHandlerServices.stream()
                .map(m -> m.handle(ingestionFlowFile, ingestionFlowFileDTO, totalNumberOfTreasuryOpiFiles))
                .filter(map -> !map.isEmpty())
                .findFirst()
                .orElseThrow(() -> new TreasuryOpiInvalidFileException("Cannot parse treasury Opi file " + ingestionFlowFile));

        List<TreasuryDTO> stringListMap = op2TreasuriesMap.get(TreasuryOperationEnum.INSERT);
        stringListMap.forEach(treasuryDTO -> {
            treasuryDao.insert(treasuryDTO);
            iufList.add(treasuryDTO.getFlowIdentifierCode());
        });

        return new TreasuryIufResult(iufList.stream().toList(), true);
    }

}
