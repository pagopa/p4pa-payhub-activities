package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class TreasuryErrorsArchiverService extends ErrorArchiverService<TreasuryErrorDTO> {

    public TreasuryErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
                                         @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                         FileArchiverService fileArchiverService,
                                         CsvService csvService) {
        super(sharedFolder, errorFolder, fileArchiverService, csvService);
    }

    @Override
    protected List<String[]> getHeaders() {
        return Collections.singletonList(
                new String[]{"FileName", "Anno Bolletta", "Codice Bolletta", "Error Code", "Error Message"});
    }

}

