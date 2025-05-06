package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionsDataExportService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.ExportFileResult;
import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.PaidInstallmentExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.exportflow.InvalidExportStatusException;
import it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition.InstallmentExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentPaidViewDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
class PaidExportFlowFileServiceTest {

    @Mock
    private CsvService csvServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;
    @Mock
    private ExportFileService exportFileServiceMock;
    @Mock
    private DebtPositionsDataExportService debtPositionsDataExportServiceMock;
    @Mock
    private InstallmentExportFlowFileDTOMapper installmentExportFlowFileDTOMapperMock;

    private final Path workingDirectory = Path.of("build","tmp");
    private final int pageSize = 20;
    private final String sharedFolder = "shared";
    private final String relativeFileFolder = Path.of("export", "paid").toString();
    private PodamFactory podamFactory;

    PaidExportFileService paidExportFlowFileService;

    @BeforeEach
    void setUp() {
        String filenamePrefix = "EXPORT";
        paidExportFlowFileService = new PaidExportFileService(csvServiceMock, fileArchiverServiceMock, workingDirectory, relativeFileFolder, filenamePrefix,sharedFolder, pageSize, exportFileServiceMock, debtPositionsDataExportServiceMock, installmentExportFlowFileDTOMapperMock);
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void givenExportFileId_WhenFindExportFileRecord_ThenReturnExportFileRecord() {
        //given
        Long exportFileId = 1L;
        PaidExportFile paidExportFile = podamFactory.manufacturePojo(PaidExportFile.class);
        when(exportFileServiceMock.findPaidExportFileById(exportFileId)).thenReturn(Optional.ofNullable(paidExportFile));
        //when
        PaidExportFile result = paidExportFlowFileService.findExportFileRecord(exportFileId);
        //then
        assertNotNull(result);
        assertEquals(paidExportFile, result);
    }

    @Test
    void givenExportFileId_WhenFindExportFileRecord_ThenThrowExportFlowFileNotFoundException() {
        //given
        Long exportFileId = 1L;

        Mockito.when(exportFileServiceMock.findPaidExportFileById(exportFileId)).thenReturn(Optional.empty());
        //when
        ExportFileNotFoundException ex = assertThrows(ExportFileNotFoundException.class,
                () -> paidExportFlowFileService.findExportFileRecord(exportFileId));
        assertEquals( "Cannot found paidExportFile having id: 1", ex.getMessage());
    }


    @Test
    void givenPaidExportFile_WhenGetOrganizationId_ThenReturnOrganizationId() {
        //given
        PaidExportFile paidExportFile = podamFactory.manufacturePojo(PaidExportFile.class);
        //when
        Long result = paidExportFlowFileService.getOrganizationId(paidExportFile);
        //then
        assertNotNull(result);
        assertEquals(paidExportFile.getOrganizationId(), result);
    }

    @Test
    void givenPaidExportFile_WhenGetFlowFileVersion_ThenReturnFlowFileVersion() {
        //given
        PaidExportFile paidExportFile = podamFactory.manufacturePojo(PaidExportFile.class);
        //when
        String result = paidExportFlowFileService.getFlowFileVersion(paidExportFile);
        //then
        assertNotNull(result);
        assertEquals(paidExportFile.getFileVersion(), result);
    }

    @Test
    void givenPaidExportFileAndFilter_WhenRetrievePage_ThenReturnInstallmentPaidViewDTOList() {
        //given
        PaidExportFile paidExportFile = podamFactory.manufacturePojo(PaidExportFile.class);
        PaidExportFileFilter paidExportFileFilter = podamFactory.manufacturePojo(PaidExportFileFilter.class);
        PagedInstallmentsPaidView pagedInstallmentsPaidView = podamFactory.manufacturePojo(PagedInstallmentsPaidView.class);

        when(debtPositionsDataExportServiceMock.exportPaidInstallments(paidExportFile.getOrganizationId(), paidExportFile.getOperatorExternalId(), paidExportFileFilter, 0, pageSize, List.of("installmentId"))).thenReturn(pagedInstallmentsPaidView);
        //when
        List<InstallmentPaidViewDTO> result = paidExportFlowFileService.retrievePage(paidExportFile, paidExportFileFilter, 0);
        //then
        assertNotNull(result);
        assertEquals(pagedInstallmentsPaidView.getContent(), result);
    }


    @Test
    void givenPaidExportFileAndFilter_WhenRetrievePage_ThenReturnCollectionEmptyList() {
        //given
        PaidExportFile paidExportFile = podamFactory.manufacturePojo(PaidExportFile.class);
        PaidExportFileFilter paidExportFileFilter = podamFactory.manufacturePojo(PaidExportFileFilter.class);

        when(debtPositionsDataExportServiceMock.exportPaidInstallments(paidExportFile.getOrganizationId(), paidExportFile.getOperatorExternalId(), paidExportFileFilter, 0, pageSize, List.of("installmentId"))).thenReturn(null);
        //when
        List<InstallmentPaidViewDTO> result = paidExportFlowFileService.retrievePage(paidExportFile, paidExportFileFilter, 0);
        //then
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void  givenPaidExportFile_WhenGetExportStatus_ThenReturnExportStatus(){
        //given
        PaidExportFile paidExportFile = podamFactory.manufacturePojo(PaidExportFile.class);
        //when
        ExportFileStatus result = paidExportFlowFileService.getExportStatus(paidExportFile);
        //then
        assertNotNull(result);
        assertEquals(paidExportFile.getStatus(), result);
    }

    @Test
    void givenInstallmentPaidViewDTO_WhenMap2Csv_ThenReturnPaidInstallmentExportFlowFileDTO() {
        //given
        InstallmentPaidViewDTO installmentPaidViewDTO = podamFactory.manufacturePojo(InstallmentPaidViewDTO.class);
        PaidInstallmentExportFlowFileDTO paidInstallmentExportFlowFileDTO = podamFactory.manufacturePojo(PaidInstallmentExportFlowFileDTO.class);
        when(installmentExportFlowFileDTOMapperMock.map(installmentPaidViewDTO)).thenReturn(paidInstallmentExportFlowFileDTO);
        //when
        PaidInstallmentExportFlowFileDTO result = paidExportFlowFileService.map2Csv(installmentPaidViewDTO);
        //then
        assertNotNull(result);
        assertEquals(paidInstallmentExportFlowFileDTO, result);
    }

    @Test
    void givenPaidExportFile_WhenGetExportFilter_ThenReturnPaidExportFileFilter() {
        //given
        PaidExportFile paidExportFile = podamFactory.manufacturePojo(PaidExportFile.class);
        //when
        PaidExportFileFilter result = paidExportFlowFileService.getExportFilter(paidExportFile);
        //then
        assertNotNull(result);
        assertEquals(paidExportFile.getFilterFields(), result);
    }

    @Test
    void givenProcessingStatus_whenExecuteExport_thenReturnsExportFlowFileResult() throws IOException {
        Long exportFileId = 1L;
        PaidExportFile paidExportFile = new PaidExportFile();
        paidExportFile.setStatus(ExportFileStatus.PROCESSING);
        paidExportFile.setOrganizationId(690213787104100L);
        paidExportFile.setFileVersion("v1");

        PagedInstallmentsPaidView pagedInstallmentsPaidView = podamFactory.manufacturePojo(PagedInstallmentsPaidView.class);
        List<InstallmentPaidViewDTO> installmentPaidViewDTOList = pagedInstallmentsPaidView.getContent();

        PaidInstallmentExportFlowFileDTO paidInstallmentExportFlowFileDTO = podamFactory.manufacturePojo(PaidInstallmentExportFlowFileDTO.class);

        when(exportFileServiceMock.findPaidExportFileById(exportFileId)).thenReturn(Optional.of(paidExportFile));
        when(debtPositionsDataExportServiceMock.exportPaidInstallments(paidExportFile.getOrganizationId(), paidExportFile.getOperatorExternalId(), null, 0, pageSize, List.of("installmentId"))).thenReturn(pagedInstallmentsPaidView);

        when(installmentExportFlowFileDTOMapperMock.map(installmentPaidViewDTOList.get(0))).thenReturn(paidInstallmentExportFlowFileDTO);
        when(installmentExportFlowFileDTOMapperMock.map(installmentPaidViewDTOList.get(1))).thenReturn(paidInstallmentExportFlowFileDTO);
        when(installmentExportFlowFileDTOMapperMock.map(installmentPaidViewDTOList.get(2))).thenReturn(paidInstallmentExportFlowFileDTO);
        when(installmentExportFlowFileDTOMapperMock.map(installmentPaidViewDTOList.get(3))).thenReturn(paidInstallmentExportFlowFileDTO);
        when(installmentExportFlowFileDTOMapperMock.map(installmentPaidViewDTOList.get(4))).thenReturn(paidInstallmentExportFlowFileDTO);

        int[] exportedRows = {0};
        doAnswer(invocation -> {
            Supplier<List<PaidInstallmentExportFlowFileDTO>> supplier = invocation.getArgument(2);
            List<PaidInstallmentExportFlowFileDTO> rows = supplier.get();
            exportedRows[0] += rows.size();
            return null;
        }).when(csvServiceMock).createCsv(any(Path.class), eq(PaidInstallmentExportFlowFileDTO.class), any(), eq("v1"));

        doNothing().when(fileArchiverServiceMock).compressAndArchive(any(), any(), any());

        // When
        ExportFileResult result = paidExportFlowFileService.executeExport(exportFileId);

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
        PaidExportFile paidExportFile = new PaidExportFile();
        paidExportFile.setStatus(ExportFileStatus.PROCESSING);
        paidExportFile.setOrganizationId(690213787104100L);
        paidExportFile.setFileVersion("v1");

        when(exportFileServiceMock.findPaidExportFileById(exportFileId)).thenReturn(Optional.of(paidExportFile));

        doThrow(IOException.class).when(csvServiceMock).createCsv(any(Path.class), eq(PaidInstallmentExportFlowFileDTO.class), any(), eq("v1"));

        // When
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> paidExportFlowFileService.executeExport(exportFileId));
        assertTrue(illegalStateException.getMessage().contains("Error writing to CSV file"));

    }

    @Test
    void givenProcessingStatus_whenCompressAndArchive_thenThrowIllegalStateException() throws IOException {
        Long exportFileId = 1L;
        PaidExportFile paidExportFile = new PaidExportFile();
        paidExportFile.setStatus(ExportFileStatus.PROCESSING);
        paidExportFile.setOrganizationId(690213787104100L);
        paidExportFile.setFileVersion("v1");

        when(exportFileServiceMock.findPaidExportFileById(exportFileId)).thenReturn(Optional.of(paidExportFile));

        doThrow(IOException.class).when(fileArchiverServiceMock).compressAndArchive(any(), any(), any());

        // When
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> paidExportFlowFileService.executeExport(exportFileId));
        assertTrue(illegalStateException.getMessage().contains("Error during compression and archiving"));

    }

    @Test
    void givenInvalidStatus_whenExecuteExport_thenThrowInvalidExportStatusException() {
        Long exportFileId = 1L;
        PaidExportFile paidExportFile = new PaidExportFile();
        paidExportFile.setStatus(ExportFileStatus.COMPLETED);
        paidExportFile.setFileVersion("v1");

        when(exportFileServiceMock.findPaidExportFileById(exportFileId)).thenReturn(Optional.of(paidExportFile));

        // When & Then
        InvalidExportStatusException invalidExportStatusException = assertThrows(InvalidExportStatusException.class, () -> paidExportFlowFileService.executeExport(exportFileId));
        assertEquals("The requested ExportFile (1) has an invalid status COMPLETED", invalidExportStatusException.getMessage());
    }
}