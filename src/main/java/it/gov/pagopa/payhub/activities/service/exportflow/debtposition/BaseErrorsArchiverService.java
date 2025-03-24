package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.export.ErrorExportDTO;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.exportflow.ExportErrorArchiverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Lazy
@Service
public class BaseErrorsArchiverService extends ExportErrorArchiverService<ErrorExportDTO> {

    protected BaseErrorsArchiverService(@Value("${folders.shared}")String sharedFolder,
                                        @Value("${folders.process-target-sub-folders.errors}")String errorFolder,
                                        FileArchiverService fileArchiverService,
                                        CsvService csvService) {
    super(Path.of(sharedFolder), errorFolder, fileArchiverService, csvService);
    }


    @Override
    protected List<String[]> getHeaders() {
        return Collections.singletonList(
                new String[]{"FileName", "Error Code", "Error Message"});

    }

}
