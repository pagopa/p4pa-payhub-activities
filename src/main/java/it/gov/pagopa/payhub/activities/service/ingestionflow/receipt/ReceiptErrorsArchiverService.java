package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileResult;
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
public class ReceiptErrorsArchiverService extends ErrorArchiverService<ReceiptErrorDTO, ReceiptIngestionFlowFileResult> {

  protected ReceiptErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
                                         @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                         FileArchiverService fileArchiverService,
                                         CsvService csvService) {
    super(sharedFolder, errorFolder, fileArchiverService, csvService);
  }

  @Override
  protected List<String[]> getHeaders(ReceiptIngestionFlowFileResult result) {
    return Collections.singletonList(
        new String[]{"File Name", "Row Number", "Error Code", "Error Message"});
  }
}
