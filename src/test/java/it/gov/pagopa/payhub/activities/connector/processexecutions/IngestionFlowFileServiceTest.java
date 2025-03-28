package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.IngestionFlowFileClient;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.PagedModelIngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.PagedModelIngestionFlowFileEmbedded;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileServiceTest {

    @Mock
    private IngestionFlowFileClient ingestionFlowFileClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private IngestionFlowFileServiceImpl ingestionFlowFileService;
    @BeforeEach
    void setUp() {
        ingestionFlowFileService = new IngestionFlowFileServiceImpl(ingestionFlowFileClientMock, authnServiceMock);
    }
    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ingestionFlowFileClientMock,
                authnServiceMock);
    }

    @Test
    void testFindById() {
        // Given
        String accessToken = "accessToken";
        Long ingestionFlowFileId = 1L;
        IngestionFlowFile expectedResponse = new IngestionFlowFile();
        when(ingestionFlowFileClientMock.findById(ingestionFlowFileId,accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

        // When
        Optional<IngestionFlowFile> result = ingestionFlowFileService.findById(ingestionFlowFileId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedResponse, result.get());
        verify(ingestionFlowFileClientMock, times(1)).findById(ingestionFlowFileId, accessToken);
    }

    @Test
    void testUpdateStatus() {
        // Given
        String accessToken = "accessToken";
        Long ingestionFlowFileId = 1L;
        IngestionFlowFileStatus oldStatus = IngestionFlowFileStatus.UPLOADED;
        IngestionFlowFileStatus newStatus = IngestionFlowFileStatus.PROCESSING;
        String errorDescription = "errorDescription";
        String discardFileName = "discardFileName";
        Integer expectedResponse = 1;
        when(ingestionFlowFileClientMock.updateStatus(ingestionFlowFileId, oldStatus, newStatus, errorDescription, discardFileName, accessToken)).thenReturn(expectedResponse);
        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        // When
        Integer result = ingestionFlowFileService.updateStatus(ingestionFlowFileId, oldStatus, newStatus, errorDescription, discardFileName);

        // Then
        assertEquals(expectedResponse, result);
        verify(ingestionFlowFileClientMock, times(1)).updateStatus(ingestionFlowFileId, oldStatus, newStatus, errorDescription, discardFileName,accessToken);
    }

    @Test
    void testFindByOrganizationIdFlowTypeCreateDate() {
        // Given
        OffsetDateTime creationDate = OffsetDateTime.now().minusDays(1);
        String accessToken = "accessToken";
        Long organizationId = 1L;
        IngestionFlowFileTypeEnum flowFileType = IngestionFlowFileTypeEnum.PAYMENTS_REPORTING;
        PagedModelIngestionFlowFileEmbedded embedded = mock(PagedModelIngestionFlowFileEmbedded.class);
        PagedModelIngestionFlowFile pagedModelIngestionFlowFile = new PagedModelIngestionFlowFile(embedded, null, null);
        List<IngestionFlowFile> expectedResponse = pagedModelIngestionFlowFile.getEmbedded().getIngestionFlowFiles();
        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        when(ingestionFlowFileClientMock.findByOrganizationIDFlowTypeCreateDate(organizationId, flowFileType, creationDate, accessToken)).thenReturn(pagedModelIngestionFlowFile);

        // When
        List<IngestionFlowFile> result = ingestionFlowFileService.findByOrganizationIdFlowTypeCreateDate(organizationId, flowFileType, creationDate);

        // Then
        assertEquals(expectedResponse, result);
        verify(ingestionFlowFileClientMock, times(1)).findByOrganizationIDFlowTypeCreateDate(organizationId, flowFileType, creationDate, accessToken);
    }

    @Test
    void testFindByOrganizationIdFlowTypeFilename() {
        // Given
        String accessToken = "accessToken";
        Long organizationId = 1L;
        IngestionFlowFileTypeEnum flowFileType = IngestionFlowFileTypeEnum.PAYMENTS_REPORTING;
        String fileName = "fileName";
        PagedModelIngestionFlowFileEmbedded embedded = mock(PagedModelIngestionFlowFileEmbedded.class);
        PagedModelIngestionFlowFile pagedModelIngestionFlowFile = new PagedModelIngestionFlowFile(embedded, null, null);
        List<IngestionFlowFile> expectedResponse = pagedModelIngestionFlowFile.getEmbedded().getIngestionFlowFiles();
        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        when(ingestionFlowFileClientMock.findByOrganizationIDFlowTypeFilename(organizationId, flowFileType, fileName, accessToken)).thenReturn(pagedModelIngestionFlowFile);

        // When
        List<IngestionFlowFile> result = ingestionFlowFileService.findByOrganizationIdFlowTypeFilename(organizationId, flowFileType, fileName);

        // Then
        assertEquals(expectedResponse, result);
        verify(ingestionFlowFileClientMock, times(1)).findByOrganizationIDFlowTypeFilename(organizationId, flowFileType, fileName, accessToken);
    }

    @Test
    void testUpdateProcessingIfNoOtherProcessing() {
        // Given
        String accessToken = "accessToken";
        Long ingestionFlowFileId = 1L;
        Integer expectedResponse = 1;
        when(ingestionFlowFileClientMock.updateProcessingIfNoOtherProcessing(ingestionFlowFileId, accessToken)).thenReturn(expectedResponse);
        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        // When
        Integer result = ingestionFlowFileService.updateProcessingIfNoOtherProcessing(ingestionFlowFileId);

        // Then
        assertEquals(expectedResponse, result);
        verify(ingestionFlowFileClientMock, times(1)).updateProcessingIfNoOtherProcessing(ingestionFlowFileId, accessToken);
    }
}