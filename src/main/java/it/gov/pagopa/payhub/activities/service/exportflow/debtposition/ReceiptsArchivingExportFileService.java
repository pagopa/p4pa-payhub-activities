package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionsDataExportService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.ReceiptsArchivingExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition.ReceiptsArchivingExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.exportflow.BaseExportFileService;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedReceiptsArchivingView;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptArchivingView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.ReceiptsArchivingExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ReceiptsArchivingExportFileFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Service
@Lazy
@Slf4j
public class ReceiptsArchivingExportFileService extends BaseExportFileService<ReceiptsArchivingExportFile, ReceiptsArchivingExportFileFilter, ReceiptArchivingView, ReceiptsArchivingExportFlowFileDTO> {

    private final int pageSize;
    private final ExportFileService exportFileService;
    private final DebtPositionsDataExportService debtPositionsDataExportService;
    private final ReceiptsArchivingExportFlowFileDTOMapper receiptsArchivingExportFlowFileDTOMapper;

    protected ReceiptsArchivingExportFileService(CsvService csvService,
                                                 FileArchiverService fileArchiverService,
                                                 @Value("${folders.tmp}") Path workingDirectory,
                                                 @Value("${export-flow-files.receipts-archiving.relative-file-folder}") String relativeFileFolder,
                                                 @Value("${export-flow-files.receipts-archiving.filename-prefix}") String fileNamePrefix,
                                                 @Value("${folders.shared}")String sharedFolder,
                                                 @Value("${export-flow-files.receipts-archiving.page-size}") int pageSize,
                                                 ExportFileService exportFileService,
                                                 DebtPositionsDataExportService debtPositionsDataExportService,
                                                 ReceiptsArchivingExportFlowFileDTOMapper receiptsArchivingExportFlowFileDTOMapper) {
        super(csvService, ReceiptsArchivingExportFlowFileDTO.class, fileArchiverService, workingDirectory, relativeFileFolder, fileNamePrefix, Path.of(sharedFolder));
        this.pageSize = pageSize;
        this.exportFileService = exportFileService;
        this.debtPositionsDataExportService = debtPositionsDataExportService;
        this.receiptsArchivingExportFlowFileDTOMapper = receiptsArchivingExportFlowFileDTOMapper;
    }

    @Override
    protected ReceiptsArchivingExportFile findExportFileRecord(Long exportFileId) {
        return exportFileService.findReceiptsArchivingExportFileById(exportFileId)
                .orElseThrow(() -> new ExportFileNotFoundException("Cannot found receiptsArchivingExportFile having id: %d".formatted(exportFileId)));
    }

    @Override
    protected Long getOrganizationId(ReceiptsArchivingExportFile exportFile) {return exportFile.getOrganizationId(); }

    @Override
    protected String getFlowFileVersion(ReceiptsArchivingExportFile exportFile) {
        return exportFile.getFileVersion();
    }

    @Override
    protected String getUpdateOperatorExternalId(ReceiptsArchivingExportFile exportFile) {
        return exportFile.getUpdateOperatorExternalId();
    }

    @Override
    protected List<ReceiptArchivingView> retrievePage(ReceiptsArchivingExportFile exportFile, ReceiptsArchivingExportFileFilter filter, int pageNumber) {
        PagedReceiptsArchivingView pagedReceiptsArchivingView = debtPositionsDataExportService.exportReceiptsArchivingView(exportFile.getOrganizationId(), exportFile.getOperatorExternalId(), filter, pageNumber, pageSize, List.of("receiptId"));
        if (pagedReceiptsArchivingView != null){
            return pagedReceiptsArchivingView.getContent();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected ReceiptsArchivingExportFlowFileDTO map2Csv(ReceiptArchivingView retrievedPage) {
        return receiptsArchivingExportFlowFileDTOMapper.map(retrievedPage);
    }

    @Override
    protected ReceiptsArchivingExportFileFilter getExportFilter(ReceiptsArchivingExportFile exportFile) {
        return exportFile.getFilterFields();
    }

    @Override
    protected ExportFileStatus getExportStatus(ReceiptsArchivingExportFile exportFile) {
        return exportFile.getStatus();
    }
}
