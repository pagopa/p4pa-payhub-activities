package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DataExportService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.PaidInstallmentExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition.InstallmentExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.exportflow.BaseExportFileService;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentPaidViewDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileFilter;
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
public class PaidExportFileService extends BaseExportFileService<PaidExportFile, PaidExportFileFilter,InstallmentPaidViewDTO, PaidInstallmentExportFlowFileDTO> {

    private final int pageSize;
    private final ExportFileService exportFileService;
    private final DataExportService dataExportService;
    private final InstallmentExportFlowFileDTOMapper installmentExportFlowFileDTOMapper;



    public PaidExportFileService(CsvService csvService,
                                 FileArchiverService fileArchiverService,
                                 @Value("${folders.tmp}") Path workingDirectory,
                                 @Value("${export-flow-files.paid.relative-file-folder}")String relativeFileFolder,
                                 @Value("${export-flow-files.paid.filename-prefix}")String filenamePrefix,
                                 @Value("${folders.shared}")String sharedFolder,
                                 @Value("${export-flow-files.paid.page-size}") int pageSize,
                                 ExportFileService exportFileService,
                                 DataExportService dataExportService,
                                 InstallmentExportFlowFileDTOMapper installmentExportFlowFileDTOMapper
                                     ) {

        super(csvService, PaidInstallmentExportFlowFileDTO.class, fileArchiverService, workingDirectory, relativeFileFolder, filenamePrefix, Path.of(sharedFolder));
        this.pageSize = pageSize;
        this.exportFileService = exportFileService;
        this.dataExportService = dataExportService;
        this.installmentExportFlowFileDTOMapper = installmentExportFlowFileDTOMapper;
    }

    @Override
    public PaidExportFile findExportFileRecord(Long exportFileId) {
        return exportFileService.findPaidExportFileById(exportFileId)
                .orElseThrow(() -> new ExportFileNotFoundException("Cannot found paidExportFile having id: %d".formatted(exportFileId)));
    }

    @Override
    public Long getOrganizationId(PaidExportFile exportFile) {
        return exportFile.getOrganizationId();
    }

    @Override
    protected String getFlowFileVersion(PaidExportFile exportFile) {
        return exportFile.getFileVersion();
    }

    @Override
    public List<InstallmentPaidViewDTO> retrievePage(PaidExportFile exportFile, PaidExportFileFilter filter, int pageNumber) {
        PagedInstallmentsPaidView pagedInstallmentsPaidView = dataExportService.exportPaidInstallments(exportFile.getOrganizationId(), exportFile.getOperatorExternalId(), filter, pageNumber, pageSize, List.of("installmentId"));
        if (pagedInstallmentsPaidView != null){
            return pagedInstallmentsPaidView.getContent();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected ExportFileStatus getExportStatus(PaidExportFile exportFile) {
        return exportFile.getStatus();
    }

    @Override
    public PaidInstallmentExportFlowFileDTO map2Csv(InstallmentPaidViewDTO retrievedInstallment) {
        return installmentExportFlowFileDTOMapper.map(retrievedInstallment);
    }

    @Override
    protected PaidExportFileFilter getExportFilter(PaidExportFile exportFile) {
        return exportFile.getFilterFields();
    }
}

