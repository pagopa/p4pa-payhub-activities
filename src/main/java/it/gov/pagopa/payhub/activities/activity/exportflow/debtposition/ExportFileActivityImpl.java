package it.gov.pagopa.payhub.activities.activity.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.ExportFileResult;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileTypeNotSupported;
import it.gov.pagopa.payhub.activities.service.exportflow.debtposition.PaidExportFileService;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class ExportFileActivityImpl implements ExportFileActivity {

    private final PaidExportFileService paidExportFileService;

    public ExportFileActivityImpl(PaidExportFileService paidExportFileService) {
        this.paidExportFileService = paidExportFileService;
    }

    @Override
    public ExportFileResult executeExport(Long exportFileId, ExportFile.ExportFileTypeEnum exportFileTypeEnum) {
        ExportFileResult exportFileResult;
        log.info("Processing exportFile {} of exportFileType {} using class {}", exportFileId, exportFileTypeEnum, getClass());

        switch (exportFileTypeEnum){
            case PAID -> exportFileResult = paidExportFileService.executeExport(exportFileId);
            default -> throw new ExportFileTypeNotSupported("Invalid export file type: " + exportFileTypeEnum);
        }

        return exportFileResult;
    }
}
