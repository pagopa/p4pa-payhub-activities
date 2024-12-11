package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.FlussoTesoreriaPIIDao;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.*;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;

import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;

import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpi14MapperService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpi161MapperService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryValidatorService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Lazy
@Component
public class TreasuryOpiIngestionActivityImpl implements TreasuryOpiIngestionActivity {

  private final String ingestionflowFileType;
  private final IngestionFlowFileDao ingestionFlowFileDao;
  private final TreasuryDao treasuryDao;
  private final FlussoTesoreriaPIIDao flussoTesoreriaPIIDao;
  private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
  private final TreasuryUnmarshallerService treasuryUnmarshallerService;
  private final TreasuryOpi14MapperService treasuryOpi14MapperService;
  private final TreasuryOpi161MapperService treasuryOpi161MapperService;
  private final TreasuryValidatorService treasuryValidatorService;


  public TreasuryOpiIngestionActivityImpl(@Value("${ingestion-flow-file-type:O}") String ingestionflowFileType,
                                          IngestionFlowFileDao ingestionFlowFileDao, TreasuryDao treasuryDao, FlussoTesoreriaPIIDao flussoTesoreriaPIIDao,
                                          IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
                                          TreasuryUnmarshallerService treasuryUnmarshallerService,
                                          TreasuryOpi14MapperService treasuryOpi14MapperService, TreasuryOpi161MapperService treasuryOpi161MapperService, TreasuryValidatorService treasuryValidatorService) {
    this.ingestionflowFileType = ingestionflowFileType;
    this.ingestionFlowFileDao = ingestionFlowFileDao;
    this.treasuryDao = treasuryDao;
    this.flussoTesoreriaPIIDao = flussoTesoreriaPIIDao;
    this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
    this.treasuryUnmarshallerService = treasuryUnmarshallerService;
    this.treasuryOpi14MapperService = treasuryOpi14MapperService;
    this.treasuryOpi161MapperService = treasuryOpi161MapperService;
    this.treasuryValidatorService = treasuryValidatorService;
  }


  @Override
  public TreasuryIngestionResultDTO processFile(Long ingestionFlowFileId) {
    List<IufIuvDTO> iufIuvList = new ArrayList<>();
    List<Path> ingestionFlowFiles = new ArrayList<>();
    IngestionFlowFileDTO ingestionFlowFileDTO = null;
    AtomicBoolean success = new AtomicBoolean(true);

    try {
      ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
              .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: " + ingestionFlowFileId));
      if (!ingestionFlowFileDTO.getFlowFileType().equals(ingestionflowFileType)) {
        throw new IllegalArgumentException("invalid ingestionFlow file type " + ingestionFlowFileDTO.getFlowFileType());
      }

      ingestionFlowFiles = ingestionFlowFileRetrieverService
              .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
    } catch (Exception e) {
      log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
      return new TreasuryIngestionResultDTO(Collections.emptyList(), false);
    }

    if (ingestionFlowFiles != null && !ingestionFlowFiles.isEmpty()) {
      List<Path> finalIngestionFlowFiles = ingestionFlowFiles;
      IngestionFlowFileDTO finalIngestionFlowFileDTO = ingestionFlowFileDTO;
      ingestionFlowFiles.forEach(path -> {
        File ingestionFlowFile = path.toFile();
        log.debug("file from zip archive with name {} loaded successfully ", ingestionFlowFile.getName());


        FlussoGiornaleDiCassa flussoGiornaleDiCassa14 = null;
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa flussoGiornaleDiCassa161 = null;
        Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> treasuryDtoMap = null;
        String versione = null;

        flussoGiornaleDiCassa161 = treasuryUnmarshallerService.unmarshalOpi161(ingestionFlowFile);
        if (flussoGiornaleDiCassa161 != null) {
          log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa161.getId());
          versione = TreasuryValidatorService.v161;
        } else {
          flussoGiornaleDiCassa14 = treasuryUnmarshallerService.unmarshalOpi14(ingestionFlowFile);
          if (flussoGiornaleDiCassa14 != null){
          log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa14.getId());
          versione = TreasuryValidatorService.v14;}
          else
            success.set(false);
        }

        assert versione != null;
//        if (!treasuryValidatorService.validatePageSize(flussoGiornaleDiCassa14, flussoGiornaleDiCassa161, finalIngestionFlowFiles.size(), versione)) {
//          log.error("invalid total page number for ingestionFlowFile with name {}", ingestionFlowFile.getName());
//          throw new RuntimeException("invalid total page number for ingestionFlowFile with name " + ingestionFlowFile.getName() + " versione " + versione);
//        }
        treasuryDtoMap = switch (versione) {
          case TreasuryValidatorService.v14 ->
                  treasuryOpi14MapperService.apply(flussoGiornaleDiCassa14, finalIngestionFlowFileDTO);
          case TreasuryValidatorService.v161 ->
                  treasuryOpi161MapperService.apply(flussoGiornaleDiCassa161, finalIngestionFlowFileDTO);
          default -> treasuryDtoMap;
        };

        treasuryDtoMap.get(TreasuryOpi161MapperService.insert).forEach(pair -> {
          long idFlussoTesoreriaPiiId = flussoTesoreriaPIIDao.insert(pair.getRight());
          TreasuryDTO treasuryDTO = pair.getLeft();
          treasuryDTO.setPersonalDataId(idFlussoTesoreriaPiiId);
          treasuryDao.insert(treasuryDTO);
          iufIuvList.add(IufIuvDTO.builder()
                  .iuf(treasuryDTO.getCodIdUnivocoFlusso())
                  .iuv(treasuryDTO.getCodIdUnivocoVersamento())
                  .build()
          );
        });


      });
    }
    return new TreasuryIngestionResultDTO(iufIuvList, success.get());
  }
}
