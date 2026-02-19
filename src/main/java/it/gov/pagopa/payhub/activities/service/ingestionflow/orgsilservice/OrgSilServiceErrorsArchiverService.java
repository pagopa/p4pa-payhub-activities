package it.gov.pagopa.payhub.activities.service.ingestionflow.orgsilservice;

import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;
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
public class OrgSilServiceErrorsArchiverService extends
    ErrorArchiverService<OrgSilServiceErrorDTO, OrgSilServiceIngestionFlowFileResult> {

  protected OrgSilServiceErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
                                               @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                               FileArchiverService fileArchiverService,
                                               CsvService csvService) {
    super(sharedFolder, errorFolder, fileArchiverService, csvService);
  }

  @Override
  protected List<String[]> getHeaders(OrgSilServiceIngestionFlowFileResult result) {
    return Collections.singletonList(
        new String[]{"File Name", "Ipa Code", "ApplicationName", "Row Number", "Error Code", "Error Message"});
  }
}
