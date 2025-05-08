package it.gov.pagopa.payhub.activities.service.exportflow.classification;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationsDataExportService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.ExportFileResult;
import it.gov.pagopa.payhub.activities.dto.exportflow.classifications.ClassificationsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.exportflow.InvalidExportStatusException;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.payhub.activities.mapper.exportflow.classifications.ClassificationsExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationViewDTO;
import it.gov.pagopa.pu.classification.dto.generated.PagedClassificationView;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFileFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassificationsExportFileServiceTest {

    @Mock
    private CsvService csvServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;
    @Mock
    private ExportFileService exportFileServiceMock;
    @Mock
    private ClassificationsDataExportService classificationsDataExportServiceMock;
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private ClassificationsExportFlowFileDTOMapper classificationsExportFlowFileDTOMapperMock;

    ClassificationsExportFileService classificationsExportFileService;

    private final Path workingDirectory = Path.of("build","tmp");
    private final int pageSize = 20;
    private final String sharedFolder = "shared";
    private final String relativeFileFolder = Path.of("export", "classifications").toString();
    private PodamFactory podamFactory;

    @BeforeEach
    void setUp() {
        String filenamePrefix = "EXPORT";
        classificationsExportFileService = new ClassificationsExportFileService(csvServiceMock, fileArchiverServiceMock, workingDirectory, relativeFileFolder,filenamePrefix , sharedFolder, exportFileServiceMock, classificationsDataExportServiceMock, classificationsExportFlowFileDTOMapperMock, organizationServiceMock, pageSize);
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void givenExportFileId_WhenFindExportFileRecord_ThenReturnExportFileRecord() {
        //given
        Long exportFileId = 1L;
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        when(exportFileServiceMock.findClassificationsExportFileById(exportFileId)).thenReturn(Optional.ofNullable(classificationsExportFile));
        //when
        ClassificationsExportFile result = classificationsExportFileService.findExportFileRecord(exportFileId);
        //then
        assertNotNull(result);
        assertEquals(classificationsExportFile, result);
    }

    @Test
    void givenExportFileId_WhenFindExportFileRecord_ThenThrowExportFlowFileNotFoundException() {
        //given
        Long exportFileId = 1L;

        when(exportFileServiceMock.findClassificationsExportFileById(exportFileId)).thenReturn(Optional.empty());
        //when
        ExportFileNotFoundException ex = assertThrows(ExportFileNotFoundException.class,
                () -> classificationsExportFileService.findExportFileRecord(exportFileId));
        assertEquals( "Cannot found classificationsExportFile having id: 1", ex.getMessage());
    }

    @Test
    void givenClassificationsExportFile_WhenGetOrganizationId_ThenReturnOrganizationId() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        //when
        Long result = classificationsExportFileService.getOrganizationId(classificationsExportFile);
        //then
        assertNotNull(result);
        assertEquals(classificationsExportFile.getOrganizationId(), result);
    }

    @Test
    void  givenClassificationsExportFile_WhenGetFlowFileVersion_ThenReturnFlowFileVersion() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        classificationsExportFile.setFileVersion("v1.4");
        //when
        String result = classificationsExportFileService.getFlowFileVersion(classificationsExportFile);
        //then
        assertNotNull(result);
        assertEquals("WITHOUT_NOTIFICATION_v1.4", result);
    }

    @Test
    void givenClassificationsExportFileAndFilter_WhenRetrievePage_ThenReturnClassificationsViewDTOList() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        ClassificationsExportFileFilter classificationsExportFileFilter = podamFactory.manufacturePojo(ClassificationsExportFileFilter.class);
        PagedClassificationView pagedClassificationView = podamFactory.manufacturePojo(PagedClassificationView.class);

        when(classificationsDataExportServiceMock.exportClassificationView(classificationsExportFile.getOrganizationId(), classificationsExportFile.getOperatorExternalId(), classificationsExportFileFilter, 0, pageSize, List.of("classificationId"))).thenReturn(pagedClassificationView);
        //when
        List<ClassificationViewDTO> result = classificationsExportFileService.retrievePage(classificationsExportFile, classificationsExportFileFilter, 0);
        //then
        assertNotNull(result);
        assertEquals(pagedClassificationView.getContent(), result);
    }

    @Test
    void givenClassificationsExportFileAndFilter_WhenRetrievePageClassificationsViewDTO_ThenReturnCollectionEmptyList() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        ClassificationsExportFileFilter classificationsExportFileFilter = podamFactory.manufacturePojo(ClassificationsExportFileFilter.class);

        when(classificationsDataExportServiceMock.exportClassificationView(classificationsExportFile.getOrganizationId(), classificationsExportFile.getOperatorExternalId(), classificationsExportFileFilter, 0, pageSize, List.of("classificationId"))).thenReturn(null);
        //when
        List<ClassificationViewDTO> result = classificationsExportFileService.retrievePage(classificationsExportFile, classificationsExportFileFilter, 0);
        //then
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void givenClassificationsExportFile_WhenGetExportStatus_ThenReturnExportStatus(){
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        //when
        ExportFileStatus result = classificationsExportFileService.getExportStatus(classificationsExportFile);
        //then
        assertNotNull(result);
        assertEquals(classificationsExportFile.getStatus(), result);
    }

    @Test
    void givenClassificationsViewDTO_WhenMap2Csv_ThenReturnClassificationsExportFlowFileDTO() {
        //given
        ClassificationsExportFlowFileDTO classificationsExportFlowFileDTO = podamFactory.manufacturePojo(ClassificationsExportFlowFileDTO.class);
        ClassificationViewDTO classificationViewDTO = podamFactory.manufacturePojo(ClassificationViewDTO.class);
        when(classificationsExportFlowFileDTOMapperMock.map(classificationViewDTO)).thenReturn(classificationsExportFlowFileDTO);
        //when
        ClassificationsExportFlowFileDTO result = classificationsExportFileService.map2Csv(classificationViewDTO);
        //then
        assertNotNull(result);
        assertEquals(classificationsExportFlowFileDTO, result);
    }


    @Test
    void givenClassificationsExportFile_WhenGetExportFilter_ThenReturnClassificationsExportFileFilter() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        //when
        ClassificationsExportFileFilter result = classificationsExportFileService.getExportFilter(classificationsExportFile);
        //then
        assertNotNull(result);
        assertEquals(classificationsExportFile.getFilterFields(), result);
    }

    @Test
    void givenExportFileId_WhenGetFlagPaymentNotification_ThenReturnClassificationsExportFileFilter() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        Organization organization = podamFactory.manufacturePojo(Organization.class);
        organization.setFlagPaymentNotification(false);

        when(exportFileServiceMock.findClassificationsExportFileById(classificationsExportFile.getExportFileId())).thenReturn(Optional.of(classificationsExportFile));
        when(organizationServiceMock.getOrganizationById(classificationsExportFile.getOrganizationId())).thenReturn(Optional.of(organization));
        //when
        boolean result = classificationsExportFileService.getFlagPaymentNotification(classificationsExportFile.getExportFileId());
        //then
        assertFalse(result);
    }

    @Test
    void givenExportFileId_WhenGetFlagPaymentNotification_ThenThrowOrganizationNotFoundException() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        classificationsExportFile.setOrganizationId(1L);

        when(exportFileServiceMock.findClassificationsExportFileById(classificationsExportFile.getExportFileId())).thenReturn(Optional.of(classificationsExportFile));
        when(organizationServiceMock.getOrganizationById(classificationsExportFile.getOrganizationId())).thenReturn(Optional.empty());
        //when
        OrganizationNotFoundException ex = assertThrows(OrganizationNotFoundException.class,
                () -> classificationsExportFileService.getFlagPaymentNotification(classificationsExportFile.getExportFileId()));
        assertEquals( "Cannot found organization having id: 1", ex.getMessage());
    }

    @Test
    void givenProcessingStatus_whenExecuteExport_thenReturnsExportFlowFileResult() throws IOException {
        Long exportFileId = 1L;
        ClassificationsExportFile classificationsExportFile = new ClassificationsExportFile();
        classificationsExportFile.setStatus(ExportFileStatus.PROCESSING);
        classificationsExportFile.setOrganizationId(690213787104100L);
        classificationsExportFile.setFileVersion("v1.3");

        PagedClassificationView pagedClassificationView = podamFactory.manufacturePojo(PagedClassificationView.class);
        List<ClassificationViewDTO> content = pagedClassificationView.getContent();

        ClassificationsExportFlowFileDTO classificationsExportFlowFileDTO = podamFactory.manufacturePojo(ClassificationsExportFlowFileDTO.class);

        when(exportFileServiceMock.findClassificationsExportFileById(exportFileId)).thenReturn(Optional.of(classificationsExportFile));
        when(classificationsDataExportServiceMock.exportClassificationView(classificationsExportFile.getOrganizationId(), classificationsExportFile.getOperatorExternalId(), null, 0, pageSize, List.of("classificationId"))).thenReturn(pagedClassificationView);

        when(classificationsExportFlowFileDTOMapperMock.map(content.getFirst())).thenReturn(classificationsExportFlowFileDTO);
        when(classificationsExportFlowFileDTOMapperMock.map(content.get(1))).thenReturn(classificationsExportFlowFileDTO);
        when(classificationsExportFlowFileDTOMapperMock.map(content.get(2))).thenReturn(classificationsExportFlowFileDTO);
        when(classificationsExportFlowFileDTOMapperMock.map(content.get(3))).thenReturn(classificationsExportFlowFileDTO);
        when(classificationsExportFlowFileDTOMapperMock.map(content.get(4))).thenReturn(classificationsExportFlowFileDTO);

        int[] exportedRows = {0};
        doAnswer(invocation -> {
            Supplier<List<ClassificationsExportFlowFileDTO>> supplier = invocation.getArgument(2);
            List<ClassificationsExportFlowFileDTO> rows = supplier.get();
            exportedRows[0] += rows.size();
            return null;
        }).when(csvServiceMock).createCsv(any(Path.class), eq(ClassificationsExportFlowFileDTO.class), any(), eq("WITHOUT_NOTIFICATION_v1.3"));

        doNothing().when(fileArchiverServiceMock).compressAndArchive(any(), any(), any());

        // When
        ExportFileResult result = classificationsExportFileService.executeExport(exportFileId);

        // Then
        assertNotNull(result);
        assertEquals("EXPORT_1.zip", result.getFileName());
        assertEquals(relativeFileFolder, result.getFilePath());
        assertEquals(5, result.getExportedRows());
        assertEquals(LocalDate.now(), result.getExportDate());
    }

    @Test
    void givenProcessingStatus_whenExecuteExport_thenThrowIllegalStateException() throws IOException {
        Long exportFileId = 1L;
        ClassificationsExportFile classificationsExportFile = new ClassificationsExportFile();
        classificationsExportFile.setStatus(ExportFileStatus.PROCESSING);
        classificationsExportFile.setOrganizationId(690213787104100L);
        classificationsExportFile.setFileVersion("v1.3");

        when(exportFileServiceMock.findClassificationsExportFileById(exportFileId)).thenReturn(Optional.of(classificationsExportFile));

        doThrow(IOException.class).when(csvServiceMock).createCsv(any(Path.class), eq(ClassificationsExportFlowFileDTO.class), any(), eq("WITHOUT_NOTIFICATION_v1.3"));

        // When
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> classificationsExportFileService.executeExport(exportFileId));
        assertTrue(illegalStateException.getMessage().contains("Error writing to CSV file"));

    }

    @Test
    void givenProcessingStatus_whenCompressAndArchive_thenThrowIllegalStateException() throws IOException {
        Long exportFileId = 1L;
        ClassificationsExportFile classificationsExportFile = new ClassificationsExportFile();
        classificationsExportFile.setStatus(ExportFileStatus.PROCESSING);
        classificationsExportFile.setOrganizationId(690213787104100L);
        classificationsExportFile.setFileVersion("v1.3");

        when(exportFileServiceMock.findClassificationsExportFileById(exportFileId)).thenReturn(Optional.of(classificationsExportFile));

        doThrow(IOException.class).when(fileArchiverServiceMock).compressAndArchive(any(), any(), any());

        // When
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> classificationsExportFileService.executeExport(exportFileId));
        assertTrue(illegalStateException.getMessage().contains("Error during compression and archiving"));
    }

    @Test
    void givenInvalidStatus_whenExecuteExport_thenThrowInvalidExportStatusException() {
        Long exportFileId = 1L;
        ClassificationsExportFile classificationsExportFile = new ClassificationsExportFile();
        classificationsExportFile.setStatus(ExportFileStatus.COMPLETED);
        classificationsExportFile.setFileVersion("v1.3");

        when(exportFileServiceMock.findClassificationsExportFileById(exportFileId)).thenReturn(Optional.of(classificationsExportFile));

        // When & Then
        InvalidExportStatusException invalidExportStatusException = assertThrows(InvalidExportStatusException.class, () -> classificationsExportFileService.executeExport(exportFileId));
        assertEquals("The requested ExportFile (1) has an invalid status COMPLETED", invalidExportStatusException.getMessage());
    }
}