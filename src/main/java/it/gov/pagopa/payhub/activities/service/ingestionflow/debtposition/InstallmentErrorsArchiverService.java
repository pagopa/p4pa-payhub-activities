package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Setter
@Lazy
@Service
public class InstallmentErrorsArchiverService extends ErrorArchiverService<InstallmentErrorDTO> {

    private String[] originalHeader;

    protected InstallmentErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
                                               @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                               FileArchiverService fileArchiverService,
                                               CsvService csvService) {
        super(sharedFolder, errorFolder, fileArchiverService, csvService);
    }

    @Override
    protected List<String[]> getHeaders() {
        String[] errorHeader =
                Arrays.copyOf(originalHeader, originalHeader.length + 2);

        errorHeader[errorHeader.length - 2] = "cod_rifiuto";
        errorHeader[errorHeader.length - 1] = "de_rifiuto";

        return Collections.singletonList(errorHeader);
    }
}
