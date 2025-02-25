package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Lazy
@Service
public class InstallmentErrorsArchiverService extends ErrorArchiverService<InstallmentErrorDTO> {

    public static final List<String[]> INSTALLMENT_HEADERS = Collections.singletonList(
            new String[]{"File Name", "IUPD", "IUD", "Workflow Status", "Row Number", "Error Code", "Error Message"}
    );

    protected InstallmentErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
                                               @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                               IngestionFlowFileArchiverService ingestionFlowFileArchiverService,
                                               CsvService csvService) {
        super(sharedFolder, errorFolder, ingestionFlowFileArchiverService, csvService);
    }

    @Override
    public void writeErrors(Path workingDirectory, IngestionFlowFile ingestionFlowFileDTO, List<InstallmentErrorDTO> errorList) {
        writeErrors(workingDirectory, ingestionFlowFileDTO, errorList, INSTALLMENT_HEADERS);
    }
}
