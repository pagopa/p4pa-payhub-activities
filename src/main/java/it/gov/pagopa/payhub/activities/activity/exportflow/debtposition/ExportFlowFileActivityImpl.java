package it.gov.pagopa.payhub.activities.activity.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.export.debtposition.ExportFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.exportFlow.InvalidExportFileException;
import it.gov.pagopa.payhub.activities.service.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.exportflow.debtposition.PaidExportFlowFileService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Lazy
@Service
public class ExportFlowFileActivityImpl implements ExportFlowFileActivity {

    private final PaidExportFlowFileService paidExportFlowFileService;
    private final FileArchiverService fileArchiverService;
    private final Path workingDirectory;

    public ExportFlowFileActivityImpl(PaidExportFlowFileService paidExportFlowFileService, FileArchiverService fileArchiverService, @Value("${folders.tmp}")Path workingDirectory) {
        this.paidExportFlowFileService = paidExportFlowFileService;
        this.fileArchiverService = fileArchiverService;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public ExportFlowFileResult executeExport(Long exportFileId, ExportFile.FlowFileTypeEnum flowFileTypeEnum) {
        ExportFlowFileResult exportFlowFileResult;
        log.info("Processing exportFlowFile {} of flowFileType {} using class {}", exportFileId, flowFileTypeEnum, getClass());

        switch (flowFileTypeEnum){
            case PAID -> exportFlowFileResult = paidExportFlowFileService.executeExport(exportFileId, workingDirectory);

            default -> exportFlowFileResult =
                    ExportFlowFileResult.builder()
                            .exportedRows(0L)
                            .build();
        }

        try {
            Path tmpZipFilePath = Path.of(exportFlowFileResult.getFilePath())
                    .getParent()
                    .resolve(Utilities.replaceFileExtension(exportFlowFileResult.getFileName(), ".zip"));
            fileArchiverService.compressAndArchive(List.of(Path.of(exportFlowFileResult.getFilePath())), tmpZipFilePath, workingDirectory);
        } catch (IOException e) {
            throw new InvalidExportFileException("Error during compression and archiving: " + e.getMessage());
        }

        return exportFlowFileResult;
    }
}
