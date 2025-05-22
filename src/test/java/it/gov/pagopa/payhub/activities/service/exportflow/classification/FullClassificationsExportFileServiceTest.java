package it.gov.pagopa.payhub.activities.service.exportflow.classification;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationsDataExportService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.ExportFileResult;
import it.gov.pagopa.payhub.activities.dto.exportflow.classifications.ClassificationsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.exportflow.InvalidExportStatusException;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.payhub.activities.mapper.exportflow.classifications.FullClassificationsExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.pu.classification.dto.generated.FullClassificationViewDTO;
import it.gov.pagopa.pu.classification.dto.generated.PagedFullClassificationView;
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
class FullClassificationsExportFileServiceTest {

    @Mock
    private CsvService csvServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;
    @Mock
    private ExportFileService exportFileServiceMock;
    @Mock
    private ClassificationsDataExportService classificationsDataExportServiceMock;
    @Mock
    private FullClassificationsExportFlowFileDTOMapper fullClassificationsExportFlowFileDTOMapperMock;
    @Mock
    private OrganizationService organizationServiceMock;

    private final Path workingDirectory = Path.of("build","tmp");
    private final int pageSize = 20;
    private final String sharedFolder = "shared";
    private final String relativeFileFolder = Path.of("export", "classifications").toString();
    private PodamFactory podamFactory;

    FullClassificationsExportFileService fullClassificationsExportFileService;

    @BeforeEach
    void setUp() {
        String filenamePrefix = "EXPORT";
        fullClassificationsExportFileService = new FullClassificationsExportFileService(csvServiceMock, fileArchiverServiceMock, workingDirectory, relativeFileFolder,filenamePrefix , sharedFolder, pageSize, exportFileServiceMock, classificationsDataExportServiceMock, fullClassificationsExportFlowFileDTOMapperMock, organizationServiceMock);
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void givenExportFileId_WhenFindExportFileRecord_ThenReturnExportFileRecord() {
        //given
        Long exportFileId = 1L;
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        when(exportFileServiceMock.findClassificationsExportFileById(exportFileId)).thenReturn(Optional.ofNullable(classificationsExportFile));
        //when
        ClassificationsExportFile result = fullClassificationsExportFileService.findExportFileRecord(exportFileId);
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
                () -> fullClassificationsExportFileService.findExportFileRecord(exportFileId));
        assertEquals( "Cannot found classificationsExportFile having id: 1", ex.getMessage());
    }

    @Test
    void givenClassificationsExportFile_WhenGetOrganizationId_ThenReturnOrganizationId() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        //when
        Long result = fullClassificationsExportFileService.getOrganizationId(classificationsExportFile);
        //then
        assertNotNull(result);
        assertEquals(classificationsExportFile.getOrganizationId(), result);
    }

    @Test
    void  givenClassificationsExportFile_WhenGetFlowFileVersion_ThenReturnFlowFileVersion() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        classificationsExportFile.setFileVersion("v1.3");
        //when
        String result = fullClassificationsExportFileService.getFlowFileVersion(classificationsExportFile);
        //then
        assertNotNull(result);
        assertEquals("WITH_NOTIFICATION_v1.3", result);
    }

    @Test
    void givenClassificationsExportFileAndFilter_WhenRetrievePage_ThenReturnFullClassificationsViewDTOList() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        ClassificationsExportFileFilter classificationsExportFileFilter = podamFactory.manufacturePojo(ClassificationsExportFileFilter.class);
        PagedFullClassificationView pagedFullClassificationView = podamFactory.manufacturePojo(PagedFullClassificationView.class);

        when(classificationsDataExportServiceMock.exportFullClassificationView(classificationsExportFile.getOrganizationId(), classificationsExportFile.getOperatorExternalId(), classificationsExportFileFilter, 0, pageSize, List.of("classificationId"))).thenReturn(pagedFullClassificationView);
        //when
        List<FullClassificationViewDTO> result = fullClassificationsExportFileService.retrievePage(classificationsExportFile, classificationsExportFileFilter, 0);
        //then
        assertNotNull(result);
        assertEquals(pagedFullClassificationView.getContent(), result);
    }

    @Test
    void givenClassificationsExportFileAndFilter_WhenRetrievePageFullClassificationsViewDTO_ThenReturnCollectionEmptyList() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        ClassificationsExportFileFilter classificationsExportFileFilter = podamFactory.manufacturePojo(ClassificationsExportFileFilter.class);

        when(classificationsDataExportServiceMock.exportFullClassificationView(classificationsExportFile.getOrganizationId(), classificationsExportFile.getOperatorExternalId(), classificationsExportFileFilter, 0, pageSize, List.of("classificationId"))).thenReturn(null);
        //when
        List<FullClassificationViewDTO> result = fullClassificationsExportFileService.retrievePage(classificationsExportFile, classificationsExportFileFilter, 0);
        //then
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void givenClassificationsExportFile_WhenGetExportStatus_ThenReturnExportStatus(){
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        //when
        ExportFileStatus result = fullClassificationsExportFileService.getExportStatus(classificationsExportFile);
        //then
        assertNotNull(result);
        assertEquals(classificationsExportFile.getStatus(), result);
    }

    @Test
    void givenFullClassificationsViewDTO_WhenMap2Csv_ThenReturnClassificationsExportFlowFileDTO() {
        //given
        ClassificationsExportFlowFileDTO classificationsExportFlowFileDTO = podamFactory.manufacturePojo(ClassificationsExportFlowFileDTO.class);
        FullClassificationViewDTO fullClassificationViewDTO = podamFactory.manufacturePojo(FullClassificationViewDTO.class);
        when(fullClassificationsExportFlowFileDTOMapperMock.map(fullClassificationViewDTO)).thenReturn(classificationsExportFlowFileDTO);
        //when
        ClassificationsExportFlowFileDTO result = fullClassificationsExportFileService.map2Csv(fullClassificationViewDTO);
        //then
        assertNotNull(result);
        assertEquals(classificationsExportFlowFileDTO, result);
    }


    @Test
    void givenClassificationsExportFile_WhenGetExportFilter_ThenReturnClassificationsExportFileFilter() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        //when
        ClassificationsExportFileFilter result = fullClassificationsExportFileService.getExportFilter(classificationsExportFile);
        //then
        assertNotNull(result);
        assertEquals(classificationsExportFile.getFilterFields(), result);
    }

    @Test
    void givenExportFileId_WhenGetFlagPaymentNotification_ThenReturnClassificationsExportFileFilter() {
        //given
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);
        Organization organization = podamFactory.manufacturePojo(Organization.class);
        organization.setFlagPaymentNotification(true);

        when(exportFileServiceMock.findClassificationsExportFileById(classificationsExportFile.getExportFileId())).thenReturn(Optional.of(classificationsExportFile));
        when(organizationServiceMock.getOrganizationById(classificationsExportFile.getOrganizationId())).thenReturn(Optional.of(organization));
        //when
        boolean result = fullClassificationsExportFileService.getFlagPaymentNotification(classificationsExportFile.getExportFileId());
        //then
        assertTrue(result);
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
                () -> fullClassificationsExportFileService.getFlagPaymentNotification(classificationsExportFile.getExportFileId()));
        assertEquals( "Cannot found organization having id: 1", ex.getMessage());
    }

    @Test
    void givenProcessingStatus_whenExecuteExport_thenReturnsExportFlowFileResult() throws IOException {
        Long exportFileId = 1L;
        ClassificationsExportFile classificationsExportFile = new ClassificationsExportFile();
        classificationsExportFile.setStatus(ExportFileStatus.PROCESSING);
        classificationsExportFile.setOrganizationId(690213787104100L);
        classificationsExportFile.setFileVersion("v1.3");

        PagedFullClassificationView pagedFullClassificationView = podamFactory.manufacturePojo(PagedFullClassificationView.class);
        List<FullClassificationViewDTO> content = pagedFullClassificationView.getContent();

        ClassificationsExportFlowFileDTO classificationsExportFlowFileDTO = podamFactory.manufacturePojo(ClassificationsExportFlowFileDTO.class);

        when(exportFileServiceMock.findClassificationsExportFileById(exportFileId)).thenReturn(Optional.of(classificationsExportFile));
        when(classificationsDataExportServiceMock.exportFullClassificationView(classificationsExportFile.getOrganizationId(), classificationsExportFile.getOperatorExternalId(), null, 0, pageSize, List.of("classificationId"))).thenReturn(pagedFullClassificationView);

        when(fullClassificationsExportFlowFileDTOMapperMock.map(content.getFirst())).thenReturn(classificationsExportFlowFileDTO);
        when(fullClassificationsExportFlowFileDTOMapperMock.map(content.get(1))).thenReturn(classificationsExportFlowFileDTO);
        when(fullClassificationsExportFlowFileDTOMapperMock.map(content.get(2))).thenReturn(classificationsExportFlowFileDTO);
        when(fullClassificationsExportFlowFileDTOMapperMock.map(content.get(3))).thenReturn(classificationsExportFlowFileDTO);
        when(fullClassificationsExportFlowFileDTOMapperMock.map(content.get(4))).thenReturn(classificationsExportFlowFileDTO);

        int[] exportedRows = {0};
        doAnswer(invocation -> {
            Supplier<List<ClassificationsExportFlowFileDTO>> supplier = invocation.getArgument(2);
            List<ClassificationsExportFlowFileDTO> rows = supplier.get();
            exportedRows[0] += rows.size();
            return null;
        }).when(csvServiceMock).createCsv(any(Path.class), eq(ClassificationsExportFlowFileDTO.class), any(), eq("WITH_NOTIFICATION_v1.3"));

        when(fileArchiverServiceMock.compressAndArchive(any(), any(), any())).thenReturn(2L);

        // When
        ExportFileResult result = fullClassificationsExportFileService.executeExport(exportFileId);

        // Then
        assertNotNull(result);
        assertEquals("EXPORT_1.zip", result.getFileName());
        assertEquals(relativeFileFolder, result.getFilePath());
        assertEquals(5, result.getExportedRows());
        assertEquals(LocalDate.now(), result.getExportDate());
        assertEquals(2L, result.getFileSize());
    }

    @Test
    void givenProcessingStatus_whenExecuteExport_thenThrowIllegalStateException() throws IOException {
        Long exportFileId = 1L;
        ClassificationsExportFile classificationsExportFile = new ClassificationsExportFile();
        classificationsExportFile.setStatus(ExportFileStatus.PROCESSING);
        classificationsExportFile.setOrganizationId(690213787104100L);
        classificationsExportFile.setFileVersion("v1.3");

        when(exportFileServiceMock.findClassificationsExportFileById(exportFileId)).thenReturn(Optional.of(classificationsExportFile));

        doThrow(IOException.class).when(csvServiceMock).createCsv(any(Path.class), eq(ClassificationsExportFlowFileDTO.class), any(), eq("WITH_NOTIFICATION_v1.3"));

        // When
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> fullClassificationsExportFileService.executeExport(exportFileId));
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
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> fullClassificationsExportFileService.executeExport(exportFileId));
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
        InvalidExportStatusException invalidExportStatusException = assertThrows(InvalidExportStatusException.class, () -> fullClassificationsExportFileService.executeExport(exportFileId));
        assertEquals("The requested ExportFile (1) has an invalid status COMPLETED", invalidExportStatusException.getMessage());
    }
}