package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtpositiontype;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontype.DebtPositionTypeProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class DebtPositionTypeIngestionActivityImpl extends
    BaseIngestionFlowFileActivity<DebtPositionTypeIngestionFlowFileResult> implements
    DebtPositionTypeIngestionActivity {


  private final CsvService csvService;
  private final DebtPositionTypeProcessingService debtPositionTypeProcessingService;

  public DebtPositionTypeIngestionActivityImpl(
      IngestionFlowFileService ingestionFlowFileService,
      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
      FileArchiverService fileArchiverService,
      CsvService csvService,
      DebtPositionTypeProcessingService debtPositionTypeProcessingService) {
    super(ingestionFlowFileService, ingestionFlowFileRetrieverService,
        fileArchiverService);
    this.csvService = csvService;

    this.debtPositionTypeProcessingService = debtPositionTypeProcessingService;
  }

  @Override
  protected IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
    return IngestionFlowFileTypeEnum.DEBT_POSITIONS_TYPE;
  }

  @Override
  protected DebtPositionTypeIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {

    Path filePath = retrievedFiles.getFirst();
    Path workingDirectory = filePath.getParent();
    log.info("Processing file: {}", filePath);

    try {
      return csvService.readCsv(filePath,
          DebtPositionTypeIngestionFlowFileDTO.class, (csvIterator, readerException) ->
              debtPositionTypeProcessingService.processDebtPositionType(csvIterator,
                  readerException,
                  ingestionFlowFileDTO, workingDirectory));
    } catch (Exception e) {
      log.error("Error processing file {}: {}", filePath, e.getMessage(), e);
      throw new InvalidIngestionFileException(
          String.format("Error processing file %s: %s", filePath, e.getMessage()));
    }
  }
}
