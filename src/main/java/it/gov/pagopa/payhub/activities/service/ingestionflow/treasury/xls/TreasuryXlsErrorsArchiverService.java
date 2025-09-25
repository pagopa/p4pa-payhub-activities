package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsErrorDTO;
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
public class TreasuryXlsErrorsArchiverService extends
        ErrorArchiverService<TreasuryXlsErrorDTO> {

    protected TreasuryXlsErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
											   @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
											   FileArchiverService fileArchiverService,
											   CsvService csvService) {
        super(sharedFolder, errorFolder, fileArchiverService, csvService);
    }

    @Override
    protected List<String[]> getHeaders() {
        return Collections.singletonList(
                new String[]{"File Name", "IUF", "Row Number", "Error Code", "Error Message"});
    }
}
