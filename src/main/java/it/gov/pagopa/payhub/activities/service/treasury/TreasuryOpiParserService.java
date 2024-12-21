package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dao.FlussoTesoreriaPIIDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.FlussoTesoreriaPIIDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Lazy
@Service
@Slf4j
public class TreasuryOpiParserService {

    private final TreasuryUnmarshallerService treasuryUnmarshallerService;
    private final TreasuryMapperService treasuryMapperService;
    private final FlussoTesoreriaPIIDao flussoTesoreriaPIIDao;
    private final TreasuryValidatorService treasuryValidatorService;

    private final TreasuryDao treasuryDao;

    public TreasuryOpiParserService(TreasuryUnmarshallerService treasuryUnmarshallerService, TreasuryMapperService treasuryMapperService, FlussoTesoreriaPIIDao flussoTesoreriaPIIDao, TreasuryValidatorService treasuryValidatorService, TreasuryDao treasuryDao) {
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
        this.treasuryMapperService = treasuryMapperService;
        this.flussoTesoreriaPIIDao = flussoTesoreriaPIIDao;
        this.treasuryValidatorService = treasuryValidatorService;
        this.treasuryDao = treasuryDao;
    }


    public TreasuryIufResult parseData(Path ingestionFlowFilePath, IngestionFlowFileDTO finalIngestionFlowFileDTO, int zipFileSize) {
        File ingestionFlowFile=ingestionFlowFilePath.toFile();
        Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> treasuryDtoMap = null;
        String versione = TreasuryValidatorService.V_161;
        Set<String> iufList = new HashSet<>();

        it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa flussoGiornaleDiCassa14 = null;
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa flussoGiornaleDiCassa161 = null;

        try {
            flussoGiornaleDiCassa161 = treasuryUnmarshallerService.unmarshalOpi161(ingestionFlowFile);
            log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa161.getId());
        } catch (Exception e) {
            log.info("file flussoGiornaleDiCassa parsing error with opi 1.6.1 format {} ", e.getMessage());
            try {
                flussoGiornaleDiCassa14 = treasuryUnmarshallerService.unmarshalOpi14(ingestionFlowFile);
                log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa14.getId());
                versione = TreasuryValidatorService.V_14;
            } catch (Exception exception) {
                log.info("file flussoGiornaleDiCassa parsing error with opi 1.4 format {} ", exception.getMessage());
                throw new TreasuryOpiInvalidFileException("Cannot parse treasury Opi file " + ingestionFlowFile);
            }
        }


        assert versione != null;
        if (!treasuryValidatorService.validatePageSize(flussoGiornaleDiCassa14, flussoGiornaleDiCassa161, zipFileSize, versione)) {
          log.error("invalid total page number for ingestionFlowFile with name {}", ingestionFlowFile.getName());
          throw new ActivitiesException("invalid total page number for ingestionFlowFile with name " + ingestionFlowFile.getName() + " version " + versione);
        }

        treasuryDtoMap = switch (versione) {
            case TreasuryValidatorService.V_14 ->
                    treasuryMapperService.apply(flussoGiornaleDiCassa14,finalIngestionFlowFileDTO);
            case TreasuryValidatorService.V_161 ->
                    treasuryMapperService.apply(flussoGiornaleDiCassa161,finalIngestionFlowFileDTO);
            default -> treasuryDtoMap;
        };

        List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>> pairs = treasuryDtoMap.get(TreasuryMapperService.INSERT);
        pairs.forEach(pair -> {
            long idFlussoTesoreriaPiiId = flussoTesoreriaPIIDao.insert(pair.getRight());
            TreasuryDTO treasuryDTO = pair.getLeft();
            treasuryDTO.setPersonalDataId(idFlussoTesoreriaPiiId);
            treasuryDao.insert(treasuryDTO);
            iufList.add(treasuryDTO.getFlowIdentifierCode());
        });

        return new TreasuryIufResult(iufList.stream().toList(), true);
    }

}
