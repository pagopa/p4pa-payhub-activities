package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.FlussoTesoreriaPIIDao;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.*;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;

import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;

import it.gov.pagopa.payhub.activities.service.treasury.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


@Slf4j
@Lazy
@Component
public class TreasuryOpiIngestionActivityImpl implements TreasuryOpiIngestionActivity {

    private final IngestionFlowFileDao ingestionFlowFileDao;
    private final TreasuryDao treasuryDao;
    private final FlussoTesoreriaPIIDao flussoTesoreriaPIIDao;
    private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
    private final TreasuryUnmarshallerService treasuryUnmarshallerService;
    private final TreasuryMapperService treasuryMapperService;


    public TreasuryOpiIngestionActivityImpl(
            IngestionFlowFileDao ingestionFlowFileDao, TreasuryDao treasuryDao, FlussoTesoreriaPIIDao flussoTesoreriaPIIDao,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            TreasuryUnmarshallerService treasuryUnmarshallerService,
            TreasuryMapperService treasuryMapperService) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.treasuryDao = treasuryDao;
        this.flussoTesoreriaPIIDao = flussoTesoreriaPIIDao;
        this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
        this.treasuryMapperService = treasuryMapperService;
    }


    @Override
    public TreasuryIufResult processFile(Long ingestionFlowFileId) {

        try {
            IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

            List<Path> ingestionFlowFiles = retrieveFiles(ingestionFlowFileDTO);


           return ingestionFlowFiles.stream()
                    .map(path -> parseData(path, ingestionFlowFileDTO))
                   .toList().get(0);

        } catch (Exception e) {
            log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
            return new TreasuryIufResult(Collections.emptyList(), false);
        }
    }

    private IngestionFlowFileDTO findIngestionFlowFileRecord(Long ingestionFlowFileId) {
        IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: " + ingestionFlowFileId));
        if (!ingestionFlowFileDTO.getFlowFileType().equals(IngestionFlowFileType.OPI)) {
            throw new IllegalArgumentException("invalid ingestionFlow file type " + ingestionFlowFileDTO.getFlowFileType());
        }
        return ingestionFlowFileDTO;
    }

    private List<Path> retrieveFiles(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {

        return ingestionFlowFileRetrieverService
                .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePath()), ingestionFlowFileDTO.getFileName());
    }

    private TreasuryIufResult parseData(Path ingestionFlowFilePath, IngestionFlowFileDTO finalIngestionFlowFileDTO) {
        File ingestionFlowFile=ingestionFlowFilePath.toFile();
        Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> treasuryDtoMap = null;
        String versione = null;
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
            } catch (Exception exception) {
                log.info("file flussoGiornaleDiCassa parsing error with opi 1.4 format {} ", exception.getMessage());
                throw new TreasuryOpiInvalidFileException("Cannot parse treasury Opi file " + ingestionFlowFile);
            }
        } else
            versione = TreasuryValidatorService.V_161;

        assert versione != null;
//        if (!treasuryValidatorService.validatePageSize(flussoGiornaleDiCassa14, flussoGiornaleDiCassa161, zipFileSize, versione)) {
//          log.error("invalid total page number for ingestionFlowFile with name {}", ingestionFlowFile.getName());
//          throw new RuntimeException("invalid total page number for ingestionFlowFile with name " + ingestionFlowFile.getName() + " versione " + versione);
//        }

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
