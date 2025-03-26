package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DataExportService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.export.debtposition.PaidInstallmentExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.exportFlow.ExportFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition.InstallmentExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.FileArchiverService;
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
public class PaidExportFlowFileService extends BaseExportFlowFileService<PaidExportFile, PaidExportFileFilter,InstallmentPaidViewDTO, PaidInstallmentExportFlowFileDTO>{

    private final int pageSize;
    private final ExportFileService exportFileService;
    private final DataExportService dataExportService;
    private final InstallmentExportFlowFileDTOMapper installmentExportFlowFileDTOMapper;



    public PaidExportFlowFileService(CsvService csvService,
                                     Class<PaidInstallmentExportFlowFileDTO> csvRowDtoClass,
                                     FileArchiverService fileArchiverService,
                                     @Value("${folders.tmp}") Path workingDirectory,
                                     @Value("${export-flow-files.paid.relative-file-folder}")String relativeFileFolder,
                                     @Value("${export-flow-files.paid.filename-prefix}")String filenamePrefix,
                                     @Value("${export-flow-files.paid.page-size}") int pageSize,
                                     ExportFileService exportFileService,
                                     DataExportService dataExportService,
                                     InstallmentExportFlowFileDTOMapper installmentExportFlowFileDTOMapper
                                     ) {

        super(csvService, csvRowDtoClass, fileArchiverService, workingDirectory, relativeFileFolder, filenamePrefix);
        this.pageSize = pageSize;
        this.exportFileService = exportFileService;
        this.dataExportService = dataExportService;
        this.installmentExportFlowFileDTOMapper = installmentExportFlowFileDTOMapper;
    }

    @Override
    public PaidExportFile findExportFileRecord(Long exportFileId) {
        return exportFileService.findPaidExportFileById(exportFileId)
                .orElseThrow(() -> new ExportFlowFileNotFoundException("Cannot found paidExportFile having id: %d".formatted(exportFileId)));
    }

    @Override
    public Long getOrganizationId(PaidExportFile exportFile) {
        return exportFile.getOrganizationId();
    }

    @Override
    protected String getFlowFileVersion(PaidExportFile exportFile) {
        return exportFile.getFlowFileVersion();
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

