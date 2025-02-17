package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.service.ingestionflow.ErrorArchiverService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Lazy
@Service
public class InstallmentErrorsArchiverService {

    private final ErrorArchiverService errorArchiverService;

    public InstallmentErrorsArchiverService(ErrorArchiverService errorArchiverService) {
        this.errorArchiverService = errorArchiverService;
    }

    public void writeInstallmentErrors(Path workingDirectory, IngestionFlowFile ingestionFlowFileDTO, List<InstallmentErrorDTO> errorList) {
        List<String> headers = List.of("File Name", "IUPD", "IUD", "Workflow Status", "Row Number", "Error Code", "Error Message");

        errorArchiverService.writeErrors(workingDirectory, ingestionFlowFileDTO, errorList, headers);
    }

    public String archiveInstallmentErrors(Path workingDirectory, IngestionFlowFile ingestionFlowFileDTO) {
        return errorArchiverService.archiveErrorFiles(workingDirectory, ingestionFlowFileDTO);
    }
}
