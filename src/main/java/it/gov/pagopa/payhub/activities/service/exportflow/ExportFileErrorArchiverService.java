package it.gov.pagopa.payhub.activities.service.exportflow;

import it.gov.pagopa.payhub.activities.dto.export.ExportFileErrorDTO;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.FileArchiverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class ExportFileErrorArchiverService extends ErrorArchiverService<ExportFileErrorDTO> {

    protected ExportFileErrorArchiverService(@Value("${folders.shared}")String sharedDirectoryPath,
                                             @Value("${folders.process-target-sub-folders.errors}")String errorFolder,
                                             FileArchiverService fileArchiverService,
                                             CsvService csvService) {
        super(sharedDirectoryPath, errorFolder, fileArchiverService, csvService);
    }

    @Override
    protected List<String[]> getHeaders() {
        return Collections.singletonList(
                new String[]{"FileName", "Error Code", "Error Message"});

    }

    @Override
    protected String[] toCsvRow(ExportFileErrorDTO error) {
        return error.toCsvRow();
    }
}
