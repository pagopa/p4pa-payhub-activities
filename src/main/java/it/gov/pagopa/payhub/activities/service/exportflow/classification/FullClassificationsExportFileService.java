package it.gov.pagopa.payhub.activities.service.exportflow.classification;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationsDataExportService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.classifications.ClassificationsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.mapper.exportflow.classifications.FullClassificationsExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.pu.classification.dto.generated.FullClassificationViewDTO;
import it.gov.pagopa.pu.classification.dto.generated.PagedFullClassificationView;
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
public class FullClassificationsExportFileService extends BaseClassificationsExportFileService<FullClassificationViewDTO> {

    private final int pageSize;
    private final ClassificationsDataExportService classificationsDataExportService;
    private final FullClassificationsExportFlowFileDTOMapper fullClassificationsExportFlowFileDTOMapper;

    protected FullClassificationsExportFileService(CsvService csvService,
                                                   FileArchiverService fileArchiverService,
                                                   @Value("${folders.tmp}") Path workingDirectory,
                                                   @Value("${export-flow-files.classifications.relative-file-folder}")String relativeFileFolder,
                                                   @Value("${export-flow-files.classifications.filename-prefix}")String filenamePrefix,
                                                   @Value("${folders.shared}")String sharedFolder,
                                                   @Value("${export-flow-files.classifications.page-size}") int pageSize,
                                                   ExportFileService exportFileService,
                                                   ClassificationsDataExportService classificationsDataExportService,
                                                   FullClassificationsExportFlowFileDTOMapper fullClassificationsExportFlowFileDTOMapper,
                                                   OrganizationService organizationService) {
        super(csvService, fileArchiverService, workingDirectory, relativeFileFolder, filenamePrefix, sharedFolder, exportFileService, organizationService);
        this.classificationsDataExportService = classificationsDataExportService;
        this.pageSize = pageSize;
        this.fullClassificationsExportFlowFileDTOMapper = fullClassificationsExportFlowFileDTOMapper;
    }

    @Override
    protected List<FullClassificationViewDTO> retrievePage(ClassificationsExportFile exportFile, ClassificationsExportFileFilter filter, int pageNumber) {
        PagedFullClassificationView pagedFullClassificationView = classificationsDataExportService.exportFullClassificationView(
                exportFile.getOrganizationId(), exportFile.getOperatorExternalId(), filter,
                pageNumber, pageSize, List.of("classificationId"));

        if (pagedFullClassificationView != null){
            return pagedFullClassificationView.getContent();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected ClassificationsExportFlowFileDTO map2Csv(FullClassificationViewDTO retrievedPage) {
        return fullClassificationsExportFlowFileDTOMapper.map(retrievedPage);
    }

    @Override
    protected String getFlowFileVersion(ClassificationsExportFile exportFile) {
        String flowFileVersion = super.getFlowFileVersion(exportFile);
        return  "WITH_NOTIFICATION_" + flowFileVersion;
    }

    @Override
    protected String getOperatorExternalId(ClassificationsExportFile exportFile) {
        return exportFile.getOperatorExternalId();
    }
}
