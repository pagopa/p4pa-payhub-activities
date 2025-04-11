package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.IngestionFlowFileClient;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
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

import static org.junit.jupiter.api.Assertions.*;
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
        IngestionFlowFileResult ingestionFlowFileResult = new IngestionFlowFileResult();
        Integer expectedResponse = 1;

        when(ingestionFlowFileClientMock.updateStatus(Mockito.same(ingestionFlowFileId), Mockito.same(oldStatus), Mockito.same(newStatus), Mockito.same(ingestionFlowFileResult), Mockito.same(accessToken)))
                .thenReturn(expectedResponse);
        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        Integer result = ingestionFlowFileService.updateStatus(ingestionFlowFileId, oldStatus, newStatus, ingestionFlowFileResult);

        // Then
        assertEquals(expectedResponse, result);
    }

    @Test
    void testFindByOrganizationIdFlowTypeCreateDate() {
        // Given
        OffsetDateTime creationDate = OffsetDateTime.now().minusDays(1);
        String accessToken = "accessToken";
        Long organizationId = 1L;
        IngestionFlowFileTypeEnum flowFileType = IngestionFlowFileTypeEnum.PAYMENTS_REPORTING;
        List<IngestionFlowFile> expectedResult = List.of();
        PagedModelIngestionFlowFileEmbedded embedded = new PagedModelIngestionFlowFileEmbedded(expectedResult);
        PagedModelIngestionFlowFile pagedModelIngestionFlowFile = new PagedModelIngestionFlowFile(embedded, null, null);

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        when(ingestionFlowFileClientMock.findByOrganizationIDFlowTypeCreateDate(organizationId, flowFileType, creationDate, accessToken))
                .thenReturn(pagedModelIngestionFlowFile);

        // When
        List<IngestionFlowFile> result = ingestionFlowFileService.findByOrganizationIdFlowTypeCreateDate(organizationId, flowFileType, creationDate);

        // Then
        assertSame(expectedResult, result);
    }

    @Test
    void testFindByOrganizationIdFlowTypeFilename() {
        // Given
        String accessToken = "accessToken";
        Long organizationId = 1L;
        IngestionFlowFileTypeEnum flowFileType = IngestionFlowFileTypeEnum.PAYMENTS_REPORTING;
        String fileName = "fileName";
        List<IngestionFlowFile> expectedResult = List.of();
        PagedModelIngestionFlowFileEmbedded embedded = new PagedModelIngestionFlowFileEmbedded(expectedResult);
        PagedModelIngestionFlowFile pagedModelIngestionFlowFile = new PagedModelIngestionFlowFile(embedded, null, null);

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        when(ingestionFlowFileClientMock.findByOrganizationIDFlowTypeFilename(organizationId, flowFileType, fileName, accessToken))
                .thenReturn(pagedModelIngestionFlowFile);

        // When
        List<IngestionFlowFile> result = ingestionFlowFileService.findByOrganizationIdFlowTypeFilename(organizationId, flowFileType, fileName);

        // Then
        assertSame(expectedResult, result);
    }

    @Test
    void testUpdateProcessingIfNoOtherProcessing() {
        // Given
        String accessToken = "accessToken";
        Long ingestionFlowFileId = 1L;
        Integer expectedResponse = 1;

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        when(ingestionFlowFileClientMock.updateProcessingIfNoOtherProcessing(ingestionFlowFileId, accessToken))
                .thenReturn(expectedResponse);

        // When
        Integer result = ingestionFlowFileService.updateProcessingIfNoOtherProcessing(ingestionFlowFileId);

        // Then
        assertSame(expectedResponse, result);
    }
}