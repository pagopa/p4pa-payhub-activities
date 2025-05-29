package it.gov.pagopa.payhub.activities.connector.processexecutions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.ExportFileClient;
import it.gov.pagopa.payhub.activities.dto.exportflow.UpdateStatusRequest;
import it.gov.pagopa.pu.processexecutions.dto.generated.*;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
class ExportFileServiceImplTest {

    @Mock
    private ExportFileClient exportFileClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private PodamFactory podamFactory;

    ExportFileService exportFileService;

    @BeforeEach
    void setUp() {
        exportFileService = new ExportFileServiceImpl(exportFileClientMock, authnServiceMock);
        podamFactory = new PodamFactoryImpl();
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                exportFileClientMock,
                authnServiceMock);
    }

    @Test
    void givenExportFileId_WhenFindPaidExportFileById_ThenReturnPaidExportFile() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        PaidExportFile paidExportFileExpected = podamFactory.manufacturePojo(PaidExportFile.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(exportFileClientMock.findPaidExportFileById(exportFileId, accessToken)).thenReturn(paidExportFileExpected);
        //when
        Optional<PaidExportFile> result = exportFileService.findPaidExportFileById(exportFileId);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(paidExportFileExpected, result.get());
    }

    @Test
    void testFindById() {
        // Given
        String accessToken = "accessToken";
        Long exportFileId = 1L;
        ExportFile expectedResponse = new ExportFile();
        when(exportFileClientMock.findById(exportFileId,accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

        // When
        Optional<ExportFile> result = exportFileService.findById(exportFileId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedResponse, result.get());
        verify(exportFileClientMock, times(1)).findById(exportFileId, accessToken);
    }

    @Test
    void testUpdateStatus() {
        // Given
        String accessToken = "accessToken";
        Long exportFileId = 1L;
        ExportFileStatus oldStatus = ExportFileStatus.COMPLETED;
        ExportFileStatus newStatus = ExportFileStatus.EXPIRED;
        String filePath = "filePath";
        String fileName = "fileName";
        Long fileSize = 20L;
        Long numTotalRows = 2L;
        String errorDescription = "errorDescription";
        Integer expectedResponse = 1;
        OffsetDateTime expirationDate = OffsetDateTime.now().plusDays(5L);

        UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest(exportFileId,
            oldStatus, newStatus, filePath, fileName, fileSize, numTotalRows, errorDescription, expirationDate);
        when(exportFileClientMock.updateStatus(updateStatusRequest, accessToken)).thenReturn(expectedResponse);
        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        // When
        Integer result = exportFileService.updateStatus(updateStatusRequest);

        // Then
        assertEquals(expectedResponse, result);
        verify(exportFileClientMock, times(1)).updateStatus(updateStatusRequest,accessToken);
    }

    @Test
    void givenExportFileId_WhenFindReceiptsArchivingFileById_ThenReturnReceiptsArchivingExportFile() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        ReceiptsArchivingExportFile receiptsArchivingExportFile = podamFactory.manufacturePojo(ReceiptsArchivingExportFile.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(exportFileClientMock.findReceiptsArchivingExportFileById(exportFileId, accessToken)).thenReturn(receiptsArchivingExportFile);
        //when
        Optional<ReceiptsArchivingExportFile> result = exportFileService.findReceiptsArchivingExportFileById(exportFileId);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(receiptsArchivingExportFile, result.get());
    }

    @Test
    void givenExportFileId_WhenFindClassificationsFileById_ThenReturnClassificationsExportFile() {
        //given
        Long exportFileId = 1L;
        String accessToken = "accessToken";
        ClassificationsExportFile classificationsExportFile = podamFactory.manufacturePojo(ClassificationsExportFile.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(exportFileClientMock.findClassificationsExportFileById(exportFileId, accessToken)).thenReturn(classificationsExportFile);
        //when
        Optional<ClassificationsExportFile> result = exportFileService.findClassificationsExportFileById(exportFileId);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(classificationsExportFile, result.get());
    }
}