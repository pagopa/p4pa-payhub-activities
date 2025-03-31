package it.gov.pagopa.payhub.activities.connector.processexecutions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.ExportFileClient;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
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
        String errorDescription = "errorDescription";
        Integer expectedResponse = 1;
        when(exportFileClientMock.updateStatus(exportFileId, oldStatus, newStatus, errorDescription, accessToken)).thenReturn(expectedResponse);
        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        // When
        Integer result = exportFileService.updateStatus(exportFileId, oldStatus, newStatus, errorDescription);

        // Then
        assertEquals(expectedResponse, result);
        verify(exportFileClientMock, times(1)).updateStatus(exportFileId, oldStatus, newStatus, errorDescription,accessToken);
    }
}