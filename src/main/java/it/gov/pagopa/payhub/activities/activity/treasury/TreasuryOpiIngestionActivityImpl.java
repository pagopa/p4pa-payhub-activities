package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.IufIuvDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIngestionResulDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;

import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;

import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpi14MapperService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interface for the TreasuryOpiIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
@Slf4j
@Lazy
@Component
public class TreasuryOpiIngestionActivityImpl implements TreasuryOpiIngestionActivity {

  private final String ingestionflowFileType;
  private final IngestionFlowFileDao ingestionFlowFileDao;
  private final TreasuryDao treasuryDao;
  private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
  private final TreasuryUnmarshallerService treasuryUnmarshallerService;
  private final TreasuryOpi14MapperService treasuryOpi14MapperService;


  public TreasuryOpiIngestionActivityImpl(@Value("${ingestion-flow-file-type:O}") String ingestionflowFileType,
                                          IngestionFlowFileDao ingestionFlowFileDao, TreasuryDao treasuryDao,
                                          IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
                                          TreasuryUnmarshallerService treasuryUnmarshallerService,
                                          TreasuryOpi14MapperService treasuryOpi14MapperService) {
    this.ingestionflowFileType = ingestionflowFileType;
    this.ingestionFlowFileDao = ingestionFlowFileDao;
    this.treasuryDao = treasuryDao;
    this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
    this.treasuryUnmarshallerService = treasuryUnmarshallerService;
    this.treasuryOpi14MapperService = treasuryOpi14MapperService;
  }


  @Override
  public TreasuryIngestionResulDTO processFile(Long ingestionFlowFileId) {
    List<IufIuvDTO> iufIuvList = new ArrayList<>();
    try {
      IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
        .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: " + ingestionFlowFileId));
      if (!ingestionFlowFileDTO.getFlowFileType().equals(ingestionflowFileType)) {
        throw new IllegalArgumentException("invalid ingestionFlow file type "+ingestionFlowFileDTO.getFlowFileType());
      }

      List<Path> ingestionFlowFiles = ingestionFlowFileRetrieverService
        .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

      ingestionFlowFiles.forEach(path -> {
          File ingestionFlowFile = path.toFile();
          FlussoGiornaleDiCassa flussoGiornaleDiCassa = treasuryUnmarshallerService.unmarshalOpi14(ingestionFlowFile);
          //  log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa.getCodIdUnivocoFlusso());


          //valida campi


          TreasuryDTO treasuryDto = treasuryOpi14MapperService.apply(flussoGiornaleDiCassa, ingestionFlowFileDTO);

          treasuryDao.insert(treasuryDto);

          iufIuvList.add(IufIuvDTO.builder()
            .iuf(treasuryDto.getCodIdUnivocoFlusso())
            .iuv(treasuryDto.getCodIdUnivocoVersamento())
            .build()
          );
        }
      );

    } catch (Exception e) {
      log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
      return new TreasuryIngestionResulDTO(Collections.emptyList(), false);
    }
    return new TreasuryIngestionResulDTO(iufIuvList, true);
  }


}
