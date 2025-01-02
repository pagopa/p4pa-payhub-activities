package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.enums.TreasuryVersionEnum;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import lombok.extern.slf4j.Slf4j;
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

    private final TreasuryBaseOpiHandlerService treasuryBaseOpiHandlerService;

    private final TreasuryDao treasuryDao;

    public TreasuryOpiParserService(TreasuryUnmarshallerService treasuryUnmarshallerService,
                                    TreasuryBaseOpiHandlerService treasuryBaseOpiHandlerService,
                                    TreasuryDao treasuryDao) {
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
        this.treasuryBaseOpiHandlerService = treasuryBaseOpiHandlerService;
        this.treasuryDao = treasuryDao;
    }


    public TreasuryIufResult parseData(Path treasuryOpiFilePath, IngestionFlowFileDTO ingestionFlowFileDTO, int totalNumberOfTreasuryOpiFiles) {
        File ingestionFlowFile = treasuryOpiFilePath.toFile();
        Map<TreasuryOperationEnum, List<TreasuryDTO>> treasuryDtoMap;
        TreasuryVersionEnum version = TreasuryVersionEnum.V_161;
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
                version = TreasuryVersionEnum.V_14;
            } catch (Exception exception) {
                log.info("file flussoGiornaleDiCassa parsing error with opi 1.4 format {} ", exception.getMessage());
                throw new TreasuryOpiInvalidFileException("Cannot parse treasury Opi file " + ingestionFlowFile);
            }
        }


        if (version == TreasuryVersionEnum.V_14) {
            TreasuryValidatorService < FlussoGiornaleDiCassa> validatorService =
                    treasuryBaseOpiHandlerService.getValidator(FlussoGiornaleDiCassa.class);
            TreasuryMapperService <FlussoGiornaleDiCassa,Map<TreasuryOperationEnum, List<TreasuryDTO>>> mapperService =
                    treasuryBaseOpiHandlerService.getMapper(FlussoGiornaleDiCassa.class);

            if (!validatorService.validatePageSize(flussoGiornaleDiCassa14, totalNumberOfTreasuryOpiFiles)) {
                log.error("invalid total page number for ingestionFlowFile with name {}", ingestionFlowFile.getName());
                throw new ActivitiesException("invalid total page number for ingestionFlowFile with name " + ingestionFlowFile.getName() + " version " + version);
            }

            treasuryDtoMap= mapperService.apply(flussoGiornaleDiCassa14, ingestionFlowFileDTO);

        } else {

            TreasuryValidatorService <it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa> validatorService =
                    treasuryBaseOpiHandlerService.getValidator(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class);
            TreasuryMapperService <it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa,Map<TreasuryOperationEnum, List<TreasuryDTO>>> mapperService =
                    treasuryBaseOpiHandlerService.getMapper(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class);

            if (!validatorService.validatePageSize(flussoGiornaleDiCassa161, totalNumberOfTreasuryOpiFiles)) {
                log.error("invalid total page number for ingestionFlowFile with name {}", ingestionFlowFile.getName());
                throw new ActivitiesException("invalid total page number for ingestionFlowFile with name " + ingestionFlowFile.getName() + " version " + version);
            }

            treasuryDtoMap= mapperService.apply(flussoGiornaleDiCassa161, ingestionFlowFileDTO);
        }


        List<TreasuryDTO> stringListMap = treasuryDtoMap.get(TreasuryOperationEnum.INSERT);
        stringListMap.forEach(treasuryDTO -> {
            treasuryDao.insert(treasuryDTO);
            iufList.add(treasuryDTO.getFlowIdentifierCode());
        });

        return new TreasuryIufResult(iufList.stream().toList(), true);
    }

}
