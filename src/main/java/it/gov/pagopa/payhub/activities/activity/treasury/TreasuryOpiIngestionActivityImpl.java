package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.IufIuvDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIngestionResultDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;

import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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
  private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;


  public TreasuryOpiIngestionActivityImpl(@Value("${ingestion-flow-file-type:O}") String ingestionflowFileType,
                                          IngestionFlowFileDao ingestionFlowFileDao,
                                          IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService) {
    this.ingestionflowFileType = ingestionflowFileType;
    this.ingestionFlowFileDao = ingestionFlowFileDao;
    this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
  }


  @Override
  public TreasuryIngestionResultDTO processFile(Long ingestionFlowFileId) {
    List<IufIuvDTO> iufIuvList = new ArrayList<>();
    try {
      IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

      List<Path> ingestionFlowFiles = retrieveFiles(ingestionFlowFileDTO);

     log.debug("Successfully retrieved the following files related to the ingestionFlowFileId {}: {}", ingestionFlowFileId, ingestionFlowFiles);

    } catch (Exception e) {
      log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
      return new TreasuryIngestionResultDTO(Collections.emptyList(), false);
    }
    return new TreasuryIngestionResultDTO(iufIuvList, true);
  }



  private IngestionFlowFileDTO findIngestionFlowFileRecord(Long ingestionFlowFileId) {
      IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
              .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: " + ingestionFlowFileId));
      if (!ingestionFlowFileDTO.getFlowFileType().equals(ingestionflowFileType)) {
        throw new IllegalArgumentException("invalid ingestionFlow file type " + ingestionFlowFileDTO.getFlowFileType());
      }
    return ingestionFlowFileDTO;
  }

  private  List<Path> retrieveFiles(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {

      return ingestionFlowFileRetrieverService
            .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
  }
}
