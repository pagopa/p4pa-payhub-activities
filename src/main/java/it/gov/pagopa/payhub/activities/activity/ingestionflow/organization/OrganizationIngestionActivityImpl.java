package it.gov.pagopa.payhub.activities.activity.ingestionflow.organization;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.organization.OrganizationProcessingService;
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
public class OrganizationIngestionActivityImpl extends BaseIngestionFlowFileActivity<OrganizationIngestionFlowFileResult> implements OrganizationIngestionActivity {

  private final CsvService csvService;
  private final OrganizationProcessingService organizationProcessingService;

  public OrganizationIngestionActivityImpl(
      IngestionFlowFileService ingestionFlowFileService,
      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
      FileArchiverService fileArchiverService,
      CsvService csvService,
      OrganizationProcessingService organizationProcessingService) {
    super(ingestionFlowFileService, ingestionFlowFileRetrieverService,
        fileArchiverService);
    this.csvService = csvService;

    this.organizationProcessingService = organizationProcessingService;
  }

  @Override
  protected IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
    return IngestionFlowFileTypeEnum.ORGANIZATIONS;
  }

  @Override
  protected OrganizationIngestionFlowFileResult handleRetrievedFiles(
      List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
    Path filePath = retrievedFiles.getFirst();
    Path workingDirectory = filePath.getParent();
    log.info("Processing file: {}", filePath);

    try {
      return csvService.readCsv(filePath,
          OrganizationIngestionFlowFileDTO.class, (csvIterator, readerException) ->
              organizationProcessingService.processOrganization(csvIterator, readerException,
                  ingestionFlowFileDTO, workingDirectory));
    } catch (Exception e) {
      log.error("Error processing file {}: {}", filePath, e.getMessage(), e);
      throw new InvalidIngestionFileException(String.format("Error processing file %s: %s", filePath, e.getMessage()));
    }

  }
}
