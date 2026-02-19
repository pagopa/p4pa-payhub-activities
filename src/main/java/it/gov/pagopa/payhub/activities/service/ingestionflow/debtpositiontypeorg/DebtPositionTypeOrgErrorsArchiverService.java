package it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontypeorg;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Lazy
@Service
public class DebtPositionTypeOrgErrorsArchiverService extends
    ErrorArchiverService<DebtPositionTypeOrgErrorDTO, DebtPositionTypeOrgIngestionFlowFileResult> {

  protected DebtPositionTypeOrgErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
      @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
      FileArchiverService fileArchiverService,
      CsvService csvService) {
    super(sharedFolder, errorFolder, fileArchiverService, csvService);
  }

  @Override
  protected List<String[]> getHeaders(DebtPositionTypeOrgIngestionFlowFileResult debtPositionTypeOrgIngestionFlowFileResult) {
    return Collections.singletonList(
        new String[]{"File Name", "Debt Position Type Code", "Organization Id", "Row Number", "Error Code", "Error Message"});
  }
}
