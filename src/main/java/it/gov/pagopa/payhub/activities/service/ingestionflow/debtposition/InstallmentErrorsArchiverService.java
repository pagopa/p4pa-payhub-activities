package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.FileArchiverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Lazy
@Service
public class InstallmentErrorsArchiverService extends ErrorArchiverService<InstallmentErrorDTO> {

    protected InstallmentErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
                                               @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                               FileArchiverService fileArchiverService,
                                               CsvService csvService) {
        super(sharedFolder, errorFolder, fileArchiverService, csvService);
    }

    @Override
    protected List<String[]> getHeaders() {
        return Collections.singletonList(
                new String[]{"File Name", "IUPD", "IUD", "Workflow Status", "Row Number", "Error Code", "Error Message"});
    }
}
