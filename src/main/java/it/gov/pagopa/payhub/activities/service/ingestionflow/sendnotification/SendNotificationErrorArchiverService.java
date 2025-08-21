package it.gov.pagopa.payhub.activities.service.ingestionflow.sendnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationErrorDTO;
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
public class SendNotificationErrorArchiverService extends ErrorArchiverService<SendNotificationErrorDTO> {

  protected SendNotificationErrorArchiverService(@Value("${folders.shared}") String sharedFolder,
      @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
      FileArchiverService fileArchiverService,
      CsvService csvService) {
    super(sharedFolder, errorFolder, fileArchiverService, csvService);
  }

  @Override
  protected List<String[]> getHeaders() {
    return Collections.singletonList(
        new String[]{"File Name", "Row Number", "Error Code", "Error Message"});
  }
}
