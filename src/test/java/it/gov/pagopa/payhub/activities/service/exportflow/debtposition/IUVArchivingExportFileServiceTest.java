package it.gov.pagopa.payhub.activities.service.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition.IUVInstallmentsExportFlowFileDTOMapper;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildIUVInstallmentsExportFlowFileDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class IUVArchivingExportFileServiceTest {

    @Mock
    private CsvService csvServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;
    @Mock
    private FoldersPathsConfig foldersPathsConfigMock;
    @Mock
    private IUVInstallmentsExportFlowFileDTOMapper iuvMapperMock;
    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private DebtPositionTypeOrgService dpTypeOrgServiceMock;

    private IUVArchivingExportFileService service;

    @BeforeEach
    void setUp() {
        service = new IUVArchivingExportFileService(
                csvServiceMock,
                fileArchiverServiceMock,
                foldersPathsConfigMock,
                iuvMapperMock,
                ingestionFlowFileServiceMock,
                dpTypeOrgServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(csvServiceMock,
                fileArchiverServiceMock,
                foldersPathsConfigMock,
                iuvMapperMock,
                ingestionFlowFileServiceMock,
                dpTypeOrgServiceMock
        );
    }
    
    @Test
    void whenExecuteExportThenOk() throws IOException {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        DebtPositionTypeOrg dpTypeOrg = new DebtPositionTypeOrg();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        Long ingestionFlowFileId = 1L;


        Mockito.when(dpTypeOrgServiceMock.getById(debtPositionDTO.getDebtPositionTypeOrgId()))
                        .thenReturn(dpTypeOrg);
        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFile));

        Mockito.when(iuvMapperMock.map(any(InstallmentDTO.class), same(dpTypeOrg)))
                        .thenReturn(buildIUVInstallmentsExportFlowFileDTO());

        Mockito.when(foldersPathsConfigMock.getTmp())
                .thenReturn(Path.of("/tmp"));

        doAnswer(invocation -> {
            Supplier<List<IUVInstallmentsExportFlowFileDTO>> supplier = invocation.getArgument(2);
            supplier.get();
            return null;
        }).when(csvServiceMock).createCsv(any(Path.class), eq(IUVInstallmentsExportFlowFileDTO.class), any(), isNull());

        Mockito.when(foldersPathsConfigMock.getShared())
                .thenReturn(Path.of("/shared"));

        Mockito.when(foldersPathsConfigMock.getProcessTargetSubFolders())
                .thenReturn(FoldersPathsConfig.ProcessTargetSubFolders.builder()
                        .archive("archive")
                        .build());


        Mockito.when(fileArchiverServiceMock.compressAndArchive(any(), any(), any()))
                .thenReturn(1L);
        
        // When
        Path result = service.executeExport(List.of(debtPositionDTO), ingestionFlowFileId);

        // Then
        assertNotNull(result);
        assertTrue(result.toString().endsWith(".zip"));
        assertEquals("fileName_iuv.zip", result.getFileName().toString());

    }

    @Test
    void givenIngestionFlowFileNotFoundWhenExecuteExportThenThrowIngestionFlowFileNotFoundException() {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        Long ingestionFlowFileId = 1L;

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.empty());

        List<DebtPositionDTO> dpList = List.of(debtPositionDTO);
        // When & Then
        IngestionFlowFileNotFoundException ex = assertThrows(IngestionFlowFileNotFoundException.class, () ->
            service.executeExport(dpList, ingestionFlowFileId));

        assertEquals("IngestionFlowFile with id 1 was not found", ex.getMessage());
    }

    @Test
    void givenErrorWritingCsvWhenExecuteExportThenThrowIOException() throws IOException {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        DebtPositionTypeOrg dpTypeOrg = new DebtPositionTypeOrg();
        Long ingestionFlowFileId = 1L;

        Mockito.when(dpTypeOrgServiceMock.getById(debtPositionDTO.getDebtPositionTypeOrgId()))
                .thenReturn(dpTypeOrg);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFile));

        Mockito.when(iuvMapperMock.map(any(InstallmentDTO.class), same(dpTypeOrg)))
                .thenReturn(buildIUVInstallmentsExportFlowFileDTO());

        Mockito.when(foldersPathsConfigMock.getTmp())
                .thenReturn(Path.of("/tmp"));

        doThrow(new IOException("Error"))
                .when(csvServiceMock).createCsv(any(Path.class), eq(IUVInstallmentsExportFlowFileDTO.class), any(), isNull());

        List<DebtPositionDTO> dpList = List.of(debtPositionDTO);

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                service.executeExport(dpList, ingestionFlowFileId));

        assertEquals("Error writing to CSV file: Error", ex.getMessage());
    }

    @Test
    void givenCreatingZipAndArchivingWhenExecuteExportThenThrowIllegalStateException() throws IOException {
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        DebtPositionTypeOrg dpTypeOrg = new DebtPositionTypeOrg();
        Long ingestionFlowFileId = 1L;

        Mockito.when(dpTypeOrgServiceMock.getById(debtPositionDTO.getDebtPositionTypeOrgId()))
                .thenReturn(dpTypeOrg);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFile));
        Mockito.when(iuvMapperMock.map(any(InstallmentDTO.class), same(dpTypeOrg)))
                .thenReturn(buildIUVInstallmentsExportFlowFileDTO());

        Mockito.when(foldersPathsConfigMock.getTmp())
                .thenReturn(Path.of("/tmp"));

        doAnswer(invocation -> {
            Supplier<List<IUVInstallmentsExportFlowFileDTO>> supplier = invocation.getArgument(2);
            supplier.get();
            return null;
        }).when(csvServiceMock).createCsv(any(Path.class), eq(IUVInstallmentsExportFlowFileDTO.class), any(), isNull());

        Mockito.when(foldersPathsConfigMock.getShared())
                .thenReturn(Path.of("/shared"));

        Mockito.when(foldersPathsConfigMock.getProcessTargetSubFolders())
                .thenReturn(FoldersPathsConfig.ProcessTargetSubFolders.builder()
                        .archive("archive")
                        .build());


        doThrow(new IOException("Error"))
                .when(fileArchiverServiceMock).compressAndArchive(any(), any(), any());

        List<DebtPositionDTO> dpList = List.of(debtPositionDTO);
        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                service.executeExport(dpList, ingestionFlowFileId));

        assertEquals("Error during compression and archiving: Error", ex.getMessage());
    }
}
