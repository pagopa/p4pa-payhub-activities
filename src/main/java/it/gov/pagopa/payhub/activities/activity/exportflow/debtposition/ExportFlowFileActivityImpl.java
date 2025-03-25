package it.gov.pagopa.payhub.activities.activity.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.export.debtposition.ExportFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.exportFlow.ExportFlowFileTypeNotSupported;
import it.gov.pagopa.payhub.activities.service.exportflow.debtposition.PaidExportFlowFileService;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class ExportFlowFileActivityImpl implements ExportFlowFileActivity {

    private final PaidExportFlowFileService paidExportFlowFileService;

    public ExportFlowFileActivityImpl(PaidExportFlowFileService paidExportFlowFileService) {
        this.paidExportFlowFileService = paidExportFlowFileService;
    }

    @Override
    public ExportFlowFileResult executeExport(Long exportFileId, ExportFile.FlowFileTypeEnum flowFileTypeEnum) {
        ExportFlowFileResult exportFlowFileResult;
        log.info("Processing exportFlowFile {} of flowFileType {} using class {}", exportFileId, flowFileTypeEnum, getClass());

        switch (flowFileTypeEnum){
            case PAID -> exportFlowFileResult = paidExportFlowFileService.executeExport(exportFileId);
            default -> throw new ExportFlowFileTypeNotSupported("Invalid export flow file type: " + flowFileTypeEnum);
        }

        return exportFlowFileResult;
    }
}
