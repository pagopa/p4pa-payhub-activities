package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.poste;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteErrorDTO;
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
public class TreasuryPosteErrorsArchiverService extends
        ErrorArchiverService<TreasuryPosteErrorDTO, TreasuryIufIngestionFlowFileResult> {

    protected TreasuryPosteErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
                                                       @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                                       FileArchiverService fileArchiverService,
                                                       CsvService csvService) {
        super(sharedFolder, errorFolder, fileArchiverService, csvService);
    }

    @Override
    protected List<String[]> getHeaders(TreasuryIufIngestionFlowFileResult result) {
        return Collections.singletonList(
                new String[]{"File Name", "IUF", "Row Number", "Error Code", "Error Message"});
    }
}
