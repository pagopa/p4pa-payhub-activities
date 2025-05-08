package it.gov.pagopa.payhub.activities.service.exportflow.classification;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.classifications.ClassificationsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.payhub.activities.service.exportflow.BaseExportFileService;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFileFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@Lazy
@Slf4j
public abstract class BaseClassificationsExportFileService <T> extends BaseExportFileService<ClassificationsExportFile, ClassificationsExportFileFilter, T, ClassificationsExportFlowFileDTO> {

    private final ExportFileService exportFileService;
    private final OrganizationService organizationService;

    protected BaseClassificationsExportFileService(CsvService csvService,
                                                   FileArchiverService fileArchiverService,
                                                   Path workingDirectory,
                                                   String relativeFileFolder,
                                                   String filenamePrefix,
                                                   String sharedFolder,
                                                   ExportFileService exportFileService, OrganizationService organizationService) {
        super(csvService, ClassificationsExportFlowFileDTO.class, fileArchiverService, workingDirectory, relativeFileFolder, filenamePrefix, Path.of(sharedFolder));
        this.exportFileService = exportFileService;
        this.organizationService = organizationService;
    }

    @Override
    protected ClassificationsExportFile findExportFileRecord(Long exportFileId) {
        return exportFileService.findClassificationsExportFileById(exportFileId)
                .orElseThrow(() -> new ExportFileNotFoundException("Cannot found classificationsExportFile having id: %d".formatted(exportFileId)));
    }

    @Override
    protected Long getOrganizationId(ClassificationsExportFile exportFile) {
        return exportFile.getOrganizationId();
    }

    @Override
    protected String getFlowFileVersion(ClassificationsExportFile exportFile) {
        return exportFile.getFileVersion();
    }

    @Override
    protected ClassificationsExportFileFilter getExportFilter(ClassificationsExportFile exportFile) {
        return exportFile.getFilterFields();
    }

    @Override
    protected ExportFileStatus getExportStatus(ClassificationsExportFile exportFile) {
        return exportFile.getStatus();
    }

    public boolean getFlagPaymentNotification(Long exportFileId){
        ClassificationsExportFile exportFileRecord = findExportFileRecord(exportFileId);
        Long organizationId = exportFileRecord.getOrganizationId();
        return organizationService.getOrganizationById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Cannot found organization having id: %d".formatted(organizationId))).getFlagPaymentNotification();
    }
}
