package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DataExportService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.export.debtposition.PaidInstallmentExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.exportFlow.ExportFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition.InstallmentExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentPaidViewDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Lazy
@Slf4j
public class PaidExportFlowFileService extends BaseExportFlowFileService<PaidExportFile, InstallmentPaidViewDTO, PaidInstallmentExportFlowFileDTO>{

    private final int pageSize;
    private final String filenamePrefix;
    private final String relativeFileFolder;
    private final ExportFileService exportFileService;
    private final DataExportService dataExportService;
    private final InstallmentExportFlowFileDTOMapper installmentExportFlowFileDTOMapper;



    public PaidExportFlowFileService(CsvService csvService,
                                     Class<PaidInstallmentExportFlowFileDTO> csvRowDtoClass,
                                     @Value("${export-flow-files.paid.page-size}") int pageSize,
                                     @Value("${export-flow-files.default.filename-prefix}")String filenamePrefix,
                                     ExportFileService exportFileService,
                                     DataExportService dataExportService,
                                     InstallmentExportFlowFileDTOMapper installmentExportFlowFileDTOMapper,
                                     BaseErrorsArchiverService baseErrorsArchiverService,
                                     @Value("${export-flow-files.paid.relative-file-folder}")String relativeFileFolder) {

        super(csvService, csvRowDtoClass, baseErrorsArchiverService);
        this.pageSize = pageSize;
        this.filenamePrefix = filenamePrefix;
        this.exportFileService = exportFileService;
        this.dataExportService = dataExportService;
        this.installmentExportFlowFileDTOMapper = installmentExportFlowFileDTOMapper;
        this.relativeFileFolder = relativeFileFolder;
    }

    @Override
    public PaidExportFile findExportFileRecord(Long exportFileId) {
        return exportFileService.findById(exportFileId)
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
    public List<InstallmentPaidViewDTO> retrievePage(PaidExportFile exportFile, int pageNumber) {
        List<InstallmentPaidViewDTO> installmentPaidViewDTOList = new ArrayList<>();

        if (exportFile.getStatus().equals(ExportFileStatus.PROCESSING)){
            PaidExportFileFilter filterFields = exportFile.getFilterFields();

            PaidExportFileFilter paidExportFileFilter = PaidExportFileFilter.builder()
                    .paymentDate(filterFields != null ? filterFields.getPaymentDate() : null)
                    .debtPositionTypeOrgId(filterFields != null ? filterFields.getDebtPositionTypeOrgId() : null)
                    .build();

            PagedInstallmentsPaidView pagedInstallmentsPaidView = dataExportService.exportPaidInstallments(exportFile.getOrganizationId(), exportFile.getOperatorExternalId(), paidExportFileFilter, pageNumber, pageSize, null);

            if (pagedInstallmentsPaidView != null){
                installmentPaidViewDTOList= pagedInstallmentsPaidView.getContent();
            }
        }else if (exportFile.getStatus().equals(ExportFileStatus.REQUESTED)){
            log.error("Paid export file status wrong during the export process for file ID %s, attempted status %s, actual %s".formatted(exportFile.getExportFileId(), ExportFileStatus.PROCESSING, ExportFileStatus.REQUESTED));
        }

        return installmentPaidViewDTOList;
    }

    @Override
    public PaidInstallmentExportFlowFileDTO map2Csv(InstallmentPaidViewDTO retrievedInstallment) {
        return installmentExportFlowFileDTOMapper.map(retrievedInstallment);
    }

    @Override
    public String getRelativeFileFolder() {
        return relativeFileFolder;
    }

    @Override
    public String getExportFileName(Long exportFileId) {
        return filenamePrefix + "_" + exportFileId + ".csv";
    }

}

