package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class TreasuryErrorsArchiverService extends ErrorArchiverService<TreasuryErrorDTO> {

    public static final List<String[]> TREASURY_HEADERS = Collections.singletonList(
            new String[]{"FileName", "Anno Bolletta", "Codice Bolletta", "Error Code", "Error Message"}
    );

    public TreasuryErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
                                         @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                         IngestionFlowFileArchiverService ingestionFlowFileArchiverService,
                                         CsvService csvService) {
        super(sharedFolder, errorFolder, ingestionFlowFileArchiverService, csvService);
    }

    @Override
    public void writeErrors(Path workingDirectory, IngestionFlowFile ingestionFlowFileDTO, List<TreasuryErrorDTO> errorList) {
        writeErrors(workingDirectory, ingestionFlowFileDTO, errorList, TREASURY_HEADERS);
    }
}

