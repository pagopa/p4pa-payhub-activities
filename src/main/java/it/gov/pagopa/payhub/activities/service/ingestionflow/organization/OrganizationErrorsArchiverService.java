package it.gov.pagopa.payhub.activities.service.ingestionflow.organization;

import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationErrorDTO;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class OrganizationErrorsArchiverService extends
    ErrorArchiverService<OrganizationErrorDTO> {

  protected OrganizationErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
      @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
      FileArchiverService fileArchiverService,
      CsvService csvService) {
    super(sharedFolder, errorFolder, fileArchiverService, csvService);
  }

  @Override
  protected List<String[]> getHeaders() {
    return Collections.singletonList(
        new String[]{"File Name", "Ipa Code", "Row Number", "Error Code", "Error Message"});
  }
}
