package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Lazy
@Service
public class InstallmentErrorsArchiverService extends ErrorArchiverService {


    protected InstallmentErrorsArchiverService(@Value("${folders.shared}") String sharedFolder,
                                               @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                               IngestionFlowFileArchiverService ingestionFlowFileArchiverService,
                                               CsvService csvService) {
        super(sharedFolder, errorFolder, ingestionFlowFileArchiverService, csvService);
    }

    public void writeErrors(Path workingDirectory, IngestionFlowFile ingestionFlowFileDTO, List<InstallmentErrorDTO> errorList) {
        List<String> headers = List.of("File Name", "IUPD", "IUD", "Workflow Status", "Row Number", "Error Code", "Error Message");
        super.writeErrors(workingDirectory, ingestionFlowFileDTO, errorList, headers);
    }
}
