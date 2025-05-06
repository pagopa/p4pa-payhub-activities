package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionsDataExportService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.ExportFileResult;
import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.ReceiptsArchivingExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.exportflow.InvalidExportStatusException;
import it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition.ReceiptsArchivingExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedReceiptsArchivingView;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptArchivingView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.ReceiptsArchivingExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ReceiptsArchivingExportFileFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
class ReceiptsArchivingExportFileServiceTest {

    @Mock
    private CsvService csvServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;
    @Mock
    private ExportFileService exportFileServiceMock;
    @Mock
    private DebtPositionsDataExportService debtPositionsDataExportServiceMock;
    @Mock
    private ReceiptsArchivingExportFlowFileDTOMapper receiptsArchivingExportFlowFileDTOMapperMock;

    private final Path workingDirectory = Path.of("build","tmp");
    private final int pageSize = 20;
    private final String sharedFolder = "shared";
    private final String relativeFileFolder = Path.of("export", "receipts-archiving").toString();
    private PodamFactory podamFactory;

    ReceiptsArchivingExportFileService receiptsArchivingExportFileService;
    @BeforeEach
    void setUp() {
        String filenamePrefix = "EXPORT";
        receiptsArchivingExportFileService =  new ReceiptsArchivingExportFileService(csvServiceMock, fileArchiverServiceMock, workingDirectory, relativeFileFolder, filenamePrefix,sharedFolder, pageSize, exportFileServiceMock, debtPositionsDataExportServiceMock, receiptsArchivingExportFlowFileDTOMapperMock);
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void givenExportFileId_WhenFindExportFileRecord_ThenReturnExportFileRecord() {
        //given
        Long exportFileId = 1L;
        ReceiptsArchivingExportFile receiptsArchivingExportFile = podamFactory.manufacturePojo(ReceiptsArchivingExportFile.class);
        when(exportFileServiceMock.findReceiptsArchivingExportFileById(exportFileId)).thenReturn(Optional.ofNullable(receiptsArchivingExportFile));
        //when
        ReceiptsArchivingExportFile result = receiptsArchivingExportFileService.findExportFileRecord(exportFileId);
        //then
        assertNotNull(result);
        assertEquals(receiptsArchivingExportFile, result);
    }

    @Test
    void givenExportFileId_WhenFindExportFileRecord_ThenThrowExportFlowFileNotFoundException() {
        //given
        Long exportFileId = 1L;

        Mockito.when(exportFileServiceMock.findReceiptsArchivingExportFileById(exportFileId)).thenReturn(Optional.empty());
        //when
        ExportFileNotFoundException ex = assertThrows(ExportFileNotFoundException.class,
                () -> receiptsArchivingExportFileService.findExportFileRecord(exportFileId));
        assertEquals( "Cannot found receiptsArchivingExportFile having id: 1", ex.getMessage());
    }


    @Test
    void givenReceiptsArchivingExportFile_WhenGetOrganizationId_ThenReturnOrganizationId() {
        //given
        ReceiptsArchivingExportFile receiptsArchivingExportFile = podamFactory.manufacturePojo(ReceiptsArchivingExportFile.class);
        //when
        Long result = receiptsArchivingExportFileService.getOrganizationId(receiptsArchivingExportFile);
        //then
        assertNotNull(result);
        assertEquals(receiptsArchivingExportFile.getOrganizationId(), result);
    }

    @Test
    void givenReceiptsArchivingExportFile_WhenGetFlowFileVersion_ThenReturnFlowFileVersion() {
        //given
        ReceiptsArchivingExportFile receiptsArchivingExportFile = podamFactory.manufacturePojo(ReceiptsArchivingExportFile.class);
        //when
        String result = receiptsArchivingExportFileService.getFlowFileVersion(receiptsArchivingExportFile);
        //then
        assertNotNull(result);
        assertEquals(receiptsArchivingExportFile.getFileVersion(), result);
    }

    @Test
    void givenReceiptsArchivingExportFileAndFilter_WhenRetrievePage_ThenReturnReceiptsReceiptArchivingViewList() {
        //given
        ReceiptsArchivingExportFile receiptsArchivingExportFile = podamFactory.manufacturePojo(ReceiptsArchivingExportFile.class);
        ReceiptsArchivingExportFileFilter receiptsArchivingExportFileFilter = podamFactory.manufacturePojo(ReceiptsArchivingExportFileFilter.class);
        PagedReceiptsArchivingView pagedReceiptsArchivingView = podamFactory.manufacturePojo(PagedReceiptsArchivingView.class);

        when(debtPositionsDataExportServiceMock.exportReceiptsArchivingView(receiptsArchivingExportFile.getOrganizationId(),receiptsArchivingExportFile.getOperatorExternalId(),receiptsArchivingExportFileFilter, 0, pageSize, List.of("receiptId"))).thenReturn(pagedReceiptsArchivingView);
        //when
        List<ReceiptArchivingView> result = receiptsArchivingExportFileService.retrievePage(receiptsArchivingExportFile, receiptsArchivingExportFileFilter, 0);
        //then
        assertNotNull(result);
        assertEquals(pagedReceiptsArchivingView.getContent(), result);
    }


    @Test
    void givenReceiptsArchivingExportFileAndFilter_WhenRetrievePage_ThenReturnCollectionEmptyList() {
        //given
        ReceiptsArchivingExportFile receiptsArchivingExportFile = podamFactory.manufacturePojo(ReceiptsArchivingExportFile.class);
        ReceiptsArchivingExportFileFilter receiptsArchivingExportFileFilter = podamFactory.manufacturePojo(ReceiptsArchivingExportFileFilter.class);

        when(debtPositionsDataExportServiceMock.exportReceiptsArchivingView(receiptsArchivingExportFile.getOrganizationId(), receiptsArchivingExportFile.getOperatorExternalId(), receiptsArchivingExportFileFilter, 0, pageSize, List.of("receiptId"))).thenReturn(null);
        //when
        List<ReceiptArchivingView> result = receiptsArchivingExportFileService.retrievePage(receiptsArchivingExportFile, receiptsArchivingExportFileFilter, 0);
        //then
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void  givenReceiptsArchivingExportFile_WhenGetExportStatus_ThenReturnExportStatus(){
        //given
        ReceiptsArchivingExportFile receiptsArchivingExportFile = podamFactory.manufacturePojo(ReceiptsArchivingExportFile.class);
        //when
        ExportFileStatus result = receiptsArchivingExportFileService.getExportStatus(receiptsArchivingExportFile);
        //then
        assertNotNull(result);
        assertEquals(receiptsArchivingExportFile.getStatus(), result);
    }

    @Test
    void givenReceiptsArchivingViewDTO_WhenMap2Csv_ThenReturnReceiptsArchivingExportFlowFileDTO() {
        //given
        ReceiptArchivingView receiptArchivingView = podamFactory.manufacturePojo(ReceiptArchivingView.class);
        ReceiptsArchivingExportFlowFileDTO receiptsArchivingExportFlowFileDTO = podamFactory.manufacturePojo(ReceiptsArchivingExportFlowFileDTO.class);
        when(receiptsArchivingExportFlowFileDTOMapperMock.map(receiptArchivingView)).thenReturn(receiptsArchivingExportFlowFileDTO);
        //when
        ReceiptsArchivingExportFlowFileDTO result = receiptsArchivingExportFileService.map2Csv(receiptArchivingView);
        //then
        assertNotNull(result);
        assertEquals(receiptsArchivingExportFlowFileDTO, result);
    }

    @Test
    void givenReceiptsArchivingExportFile_WhenGetExportFilter_ThenReturnReceiptsArchivingExportFileFilter() {
        //given
        ReceiptsArchivingExportFile receiptsArchivingExportFile = podamFactory.manufacturePojo(ReceiptsArchivingExportFile.class);
        //when
        ReceiptsArchivingExportFileFilter result = receiptsArchivingExportFileService.getExportFilter(receiptsArchivingExportFile);
        //then
        assertNotNull(result);
        assertEquals(receiptsArchivingExportFile.getFilterFields(), result);
    }

    @Test
    void givenProcessingStatus_whenExecuteExport_thenReturnsExportFlowFileResult() throws IOException {
        Long exportFileId = 1L;
        ReceiptsArchivingExportFile receiptsArchivingExportFile = new ReceiptsArchivingExportFile();
        receiptsArchivingExportFile.setStatus(ExportFileStatus.PROCESSING);
        receiptsArchivingExportFile.setOrganizationId(690213787104100L);
        receiptsArchivingExportFile.setFileVersion("v1");

        PagedReceiptsArchivingView pagedReceiptsArchivingView = podamFactory.manufacturePojo(PagedReceiptsArchivingView.class);
        List<ReceiptArchivingView> content = pagedReceiptsArchivingView.getContent();

        ReceiptsArchivingExportFlowFileDTO receiptsArchivingExportFlowFileDTO = podamFactory.manufacturePojo(ReceiptsArchivingExportFlowFileDTO.class);

            when(exportFileServiceMock.findReceiptsArchivingExportFileById(exportFileId)).thenReturn(Optional.of(receiptsArchivingExportFile));
        when(debtPositionsDataExportServiceMock.exportReceiptsArchivingView(receiptsArchivingExportFile.getOrganizationId(), receiptsArchivingExportFile.getOperatorExternalId(), null, 0, pageSize, List.of("receiptId"))).thenReturn(pagedReceiptsArchivingView);

        when(receiptsArchivingExportFlowFileDTOMapperMock.map(content.getFirst())).thenReturn(receiptsArchivingExportFlowFileDTO);
        when(receiptsArchivingExportFlowFileDTOMapperMock.map(content.get(1))).thenReturn(receiptsArchivingExportFlowFileDTO);
        when(receiptsArchivingExportFlowFileDTOMapperMock.map(content.get(2))).thenReturn(receiptsArchivingExportFlowFileDTO);
        when(receiptsArchivingExportFlowFileDTOMapperMock.map(content.get(3))).thenReturn(receiptsArchivingExportFlowFileDTO);
        when(receiptsArchivingExportFlowFileDTOMapperMock.map(content.get(4))).thenReturn(receiptsArchivingExportFlowFileDTO);

        int[] exportedRows = {0};
        doAnswer(invocation -> {
            Supplier<List<ReceiptsArchivingExportFlowFileDTO>> supplier = invocation.getArgument(2);
            List<ReceiptsArchivingExportFlowFileDTO> rows = supplier.get();
            exportedRows[0] += rows.size();
            return null;
        }).when(csvServiceMock).createCsv(any(Path.class), eq(ReceiptsArchivingExportFlowFileDTO.class), any(), eq("v1"));

        doNothing().when(fileArchiverServiceMock).compressAndArchive(any(), any(), any());

        // When
        ExportFileResult result = receiptsArchivingExportFileService.executeExport(exportFileId);

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
        ReceiptsArchivingExportFile receiptsArchivingExportFile = new ReceiptsArchivingExportFile();
        receiptsArchivingExportFile.setStatus(ExportFileStatus.PROCESSING);
        receiptsArchivingExportFile.setOrganizationId(690213787104100L);
        receiptsArchivingExportFile.setFileVersion("v1");

        when(exportFileServiceMock.findReceiptsArchivingExportFileById(exportFileId)).thenReturn(Optional.of(receiptsArchivingExportFile));

        doThrow(IOException.class).when(csvServiceMock).createCsv(any(Path.class), eq(ReceiptsArchivingExportFlowFileDTO.class), any(), eq("v1"));

        // When
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> receiptsArchivingExportFileService.executeExport(exportFileId));
        assertTrue(illegalStateException.getMessage().contains("Error writing to CSV file"));

    }

    @Test
    void givenProcessingStatus_whenCompressAndArchive_thenThrowIllegalStateException() throws IOException {
        Long exportFileId = 1L;
        ReceiptsArchivingExportFile receiptsArchivingExportFile = new ReceiptsArchivingExportFile();
        receiptsArchivingExportFile.setStatus(ExportFileStatus.PROCESSING);
        receiptsArchivingExportFile.setOrganizationId(690213787104100L);
        receiptsArchivingExportFile.setFileVersion("v1");

        when(exportFileServiceMock.findReceiptsArchivingExportFileById(exportFileId)).thenReturn(Optional.of(receiptsArchivingExportFile));

        doThrow(IOException.class).when(fileArchiverServiceMock).compressAndArchive(any(), any(), any());

        // When
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> receiptsArchivingExportFileService.executeExport(exportFileId));
        assertTrue(illegalStateException.getMessage().contains("Error during compression and archiving"));

    }

    @Test
    void givenInvalidStatus_whenExecuteExport_thenThrowInvalidExportStatusException() {
        Long exportFileId = 1L;
        ReceiptsArchivingExportFile receiptsArchivingExportFile = new ReceiptsArchivingExportFile();
        receiptsArchivingExportFile.setStatus(ExportFileStatus.COMPLETED);
        receiptsArchivingExportFile.setFileVersion("v1");

        when(exportFileServiceMock.findReceiptsArchivingExportFileById(exportFileId)).thenReturn(Optional.of(receiptsArchivingExportFile));

        // When & Then
        InvalidExportStatusException invalidExportStatusException = assertThrows(InvalidExportStatusException.class, () -> receiptsArchivingExportFileService.executeExport(exportFileId));
        assertEquals("The requested ExportFile (1) has an invalid status COMPLETED", invalidExportStatusException.getMessage());
    }
}