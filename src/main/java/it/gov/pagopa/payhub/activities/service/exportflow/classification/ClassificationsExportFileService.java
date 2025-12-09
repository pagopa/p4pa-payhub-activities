package it.gov.pagopa.payhub.activities.service.exportflow.classification;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationsDataExportService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.classifications.ClassificationsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.mapper.exportflow.classifications.ClassificationsExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationViewDTO;
import it.gov.pagopa.pu.classification.dto.generated.PagedClassificationView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFileFilter;
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
public class ClassificationsExportFileService extends BaseClassificationsExportFileService<ClassificationViewDTO> {

    private final int pageSize;
    private final ClassificationsDataExportService classificationsDataExportService;
    private final ClassificationsExportFlowFileDTOMapper classificationsExportFlowFileDTOMapper;

    protected ClassificationsExportFileService(CsvService csvService,
                                            FileArchiverService fileArchiverService,
                                            @Value("${folders.tmp}") Path workingDirectory,
                                            @Value("${export-flow-files.classifications.relative-file-folder}")String relativeFileFolder,
                                            @Value("${export-flow-files.classifications.filename-prefix}")String filenamePrefix,
                                            @Value("${folders.shared}")String sharedFolder,
                                            ExportFileService exportFileService,
                                            ClassificationsDataExportService classificationsDataExportService,
                                            ClassificationsExportFlowFileDTOMapper classificationsExportFlowFileDTOMapper,
                                            OrganizationService organizationService,
                                            @Value("${export-flow-files.classifications.page-size}") int pageSize) {
        super(csvService, fileArchiverService, workingDirectory, relativeFileFolder, filenamePrefix, sharedFolder, exportFileService, organizationService);
        this.classificationsDataExportService = classificationsDataExportService;
        this.classificationsExportFlowFileDTOMapper = classificationsExportFlowFileDTOMapper;
        this.pageSize = pageSize;
    }

    @Override
    protected List<ClassificationViewDTO> retrievePage(ClassificationsExportFile exportFile, ClassificationsExportFileFilter filter, int pageNumber) {
        PagedClassificationView pagedClassificationView = classificationsDataExportService.exportClassificationView(
                exportFile.getOrganizationId(), exportFile.getOperatorExternalId(), filter,
                pageNumber, pageSize, List.of("classificationId"));

        if (pagedClassificationView != null){
            return pagedClassificationView.getContent();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected ClassificationsExportFlowFileDTO map2Csv(ClassificationViewDTO retrievedPage) {
        return classificationsExportFlowFileDTOMapper.map(retrievedPage);
    }

    @Override
    protected String getFlowFileVersion(ClassificationsExportFile exportFile) {
        String flowFileVersion = super.getFlowFileVersion(exportFile);
        return "WITHOUT_NOTIFICATION_" + flowFileVersion;
    }

    @Override
    protected String getUpdateOperatorExternalId(ClassificationsExportFile exportFile) {
        return exportFile.getUpdateOperatorExternalId();
    }
}
