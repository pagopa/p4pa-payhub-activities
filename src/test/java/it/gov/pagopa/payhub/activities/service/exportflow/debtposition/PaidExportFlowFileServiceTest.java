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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaidExportFlowFileServiceTest {

    @Mock
    private CsvService csvServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;
    @Mock
    private ExportFileService exportFileServiceMock;
    @Mock
    private DataExportService dataExportServiceMock;
    @Mock
    private InstallmentExportFlowFileDTOMapper installmentExportFlowFileDTOMapperMock;

    private final Path workingDirectory = Path.of("build","tmp");
    private final int pageSize = 20;
    private final Class<PaidInstallmentExportFlowFileDTO> csvRowDtoClass = PaidInstallmentExportFlowFileDTO.class ;
    private final String sharedFolder = "shared";
    private PodamFactory podamFactory;

    PaidExportFlowFileService paidExportFlowFileService;

    @BeforeEach
    void setUp() {
        String filenamePrefix = "EXPORT";
        String relativeFileFolder = "export/paid";
        paidExportFlowFileService = new PaidExportFlowFileService(csvServiceMock, csvRowDtoClass, fileArchiverServiceMock, workingDirectory, relativeFileFolder, filenamePrefix,sharedFolder, pageSize, exportFileServiceMock, dataExportServiceMock, installmentExportFlowFileDTOMapperMock);
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
        ExportFlowFileNotFoundException ex = assertThrows(ExportFlowFileNotFoundException.class,
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

        when(dataExportServiceMock.exportPaidInstallments(paidExportFile.getOrganizationId(), paidExportFile.getOperatorExternalId(), paidExportFileFilter, 0, pageSize, List.of("installmentId"))).thenReturn(pagedInstallmentsPaidView);
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

        when(dataExportServiceMock.exportPaidInstallments(paidExportFile.getOrganizationId(), paidExportFile.getOperatorExternalId(), paidExportFileFilter, 0, pageSize, List.of("installmentId"))).thenReturn(null);
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
}