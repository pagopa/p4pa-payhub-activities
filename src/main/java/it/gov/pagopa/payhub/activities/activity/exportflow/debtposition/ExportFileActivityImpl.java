package it.gov.pagopa.payhub.activities.activity.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.ExportFileResult;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileTypeNotSupported;
import it.gov.pagopa.payhub.activities.service.exportflow.debtposition.PaidExportFileService;
import it.gov.pagopa.payhub.activities.service.exportflow.debtposition.ReceiptsArchivingExportFileService;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class ExportFileActivityImpl implements ExportFileActivity {

    private final PaidExportFileService paidExportFileService;
    private final ReceiptsArchivingExportFileService receiptsArchivingExportFileService;

    public ExportFileActivityImpl(PaidExportFileService paidExportFileService, ReceiptsArchivingExportFileService receiptsArchivingExportFileService) {
        this.paidExportFileService = paidExportFileService;
        this.receiptsArchivingExportFileService = receiptsArchivingExportFileService;
    }

    @Override
    public ExportFileResult executeExport(Long exportFileId, ExportFile.ExportFileTypeEnum exportFileTypeEnum) {
        ExportFileResult exportFileResult;
        log.info("Processing exportFile {} of exportFileType {} using class {}", exportFileId, exportFileTypeEnum, getClass());

        switch (exportFileTypeEnum){
            case PAID -> exportFileResult = paidExportFileService.executeExport(exportFileId);
            case RECEIPTS_ARCHIVING -> exportFileResult = receiptsArchivingExportFileService.executeExport(exportFileId);
            default -> throw new ExportFileTypeNotSupported("Invalid export file type: " + exportFileTypeEnum);
        }

        return exportFileResult;
    }
}
