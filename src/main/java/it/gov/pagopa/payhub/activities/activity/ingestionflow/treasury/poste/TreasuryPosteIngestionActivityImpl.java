package it.gov.pagopa.payhub.activities.activity.ingestionflow.treasury.poste;

import com.google.common.io.Files;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.poste.TreasuryPosteProcessingService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class TreasuryPosteIngestionActivityImpl
    extends BaseIngestionFlowFileActivity<TreasuryIufIngestionFlowFileResult>
    implements TreasuryPosteIngestionActivity {

  private final CsvService csvService;
  private final TreasuryPosteProcessingService treasuryPosteProcessingService;

  protected TreasuryPosteIngestionActivityImpl(
      IngestionFlowFileService ingestionFlowFileService,
      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
      FileArchiverService fileArchiverService,
      CsvService csvService,
      TreasuryPosteProcessingService treasuryPosteProcessingService) {

    super(ingestionFlowFileService, ingestionFlowFileRetrieverService, fileArchiverService);

    this.csvService = csvService;
    this.treasuryPosteProcessingService = treasuryPosteProcessingService;
  }

  @Override
  protected IngestionFlowFile.IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
    return IngestionFlowFileTypeEnum.TREASURY_POSTE;
  }

  @Override
  protected TreasuryIufIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {

    if (retrievedFiles.size() > 1) {
      String msg = String.format(
          "Multiple files [%s] found for ingestion flow file ID %s. Only the first file will be processed.",
          retrievedFiles.size(), ingestionFlowFileDTO.getIngestionFlowFileId());
      log.error(msg);
      throw new InvalidIngestionFileException(msg);
    }

    Path filePath = retrievedFiles.getFirst();
    Path workingDirectory = filePath.getParent();
    log.info("Processing file: {}", filePath);

    try {
      String firstLine = Files.asCharSource(filePath.toFile(), StandardCharsets.UTF_8).readFirstLine();
      String iban = Utilities.extractIban(firstLine);

      return csvService.readCsvPositionalColumn(filePath,
          TreasuryPosteIngestionFlowFileDTO.class,
          (csvIterator, readerException) ->
              treasuryPosteProcessingService.processTreasuryPoste(csvIterator, iban, readerException, ingestionFlowFileDTO, workingDirectory),
          null, 1);

    } catch (Exception e) {
      log.error("Error processing file {}: {}", filePath, e.getMessage(), e);
      throw new InvalidIngestionFileException(
          String.format("Error processing file %s: %s", filePath, e.getMessage()));
    }
  }
}
