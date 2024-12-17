package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
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
  private final IngestionFlowFileType ingestionflowFileType;
  private final IngestionFlowFileDao ingestionFlowFileDao;
  private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;


  public TreasuryOpiIngestionActivityImpl(IngestionFlowFileDao ingestionFlowFileDao,
                                          IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService) {
    this.ingestionflowFileType = IngestionFlowFileType.OPI;
    this.ingestionFlowFileDao = ingestionFlowFileDao;
    this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
  }


  @Override
  public TreasuryIufResult processFile(Long ingestionFlowFileId) {
    List<String> iufIuvList = new ArrayList<>();
    try {
      IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

      List<Path> ingestionFlowFiles = retrieveFiles(ingestionFlowFileDTO);

     log.debug("Successfully retrieved the following files related to the ingestionFlowFileId {}: {}", ingestionFlowFileId, ingestionFlowFiles);

    } catch (Exception e) {
      log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
      return new TreasuryIufResult(Collections.emptyList(), false);
    }
    return new TreasuryIufResult(iufIuvList, true);
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
            .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePath()), ingestionFlowFileDTO.getFileName());
  }
}
