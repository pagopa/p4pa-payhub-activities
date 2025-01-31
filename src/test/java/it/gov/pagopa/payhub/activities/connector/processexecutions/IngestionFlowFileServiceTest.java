package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.IngestionFlowFileClient;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.FlowFileTypeEnum;
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
        IngestionFlowFile.StatusEnum status = IngestionFlowFile.StatusEnum.PROCESSING;
        String codError = "codError";
        String discardFileName = "discardFileName";
        Integer expectedResponse = 1;
        when(ingestionFlowFileClientMock.updateStatus(ingestionFlowFileId, status, codError, discardFileName, accessToken)).thenReturn(expectedResponse);
        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        // When
        Integer result = ingestionFlowFileService.updateStatus(ingestionFlowFileId, status, codError, discardFileName);

        // Then
        assertEquals(expectedResponse, result);
        verify(ingestionFlowFileClientMock, times(1)).updateStatus(ingestionFlowFileId, status, codError, discardFileName,accessToken);
    }

    @Test
    void testFindByOrganizationIdFlowTypeCreateDate() {
        // Given
        OffsetDateTime creationDate = OffsetDateTime.now();
        String accessToken = "accessToken";
        Long organizationId = 1L;
        FlowFileTypeEnum flowFileType = FlowFileTypeEnum.PAYMENTS_REPORTING;
        PagedModelIngestionFlowFileEmbedded embedded = mock(PagedModelIngestionFlowFileEmbedded.class);
        PagedModelIngestionFlowFile pagedModelIngestionFlowFile = new PagedModelIngestionFlowFile(embedded, null, null);
        List<IngestionFlowFile> expectedResponse = pagedModelIngestionFlowFile.getEmbedded().getIngestionFlowFiles();
        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        when(ingestionFlowFileClientMock.findByOrganizationIDFlowTypeCreateDate(organizationId, flowFileType.getValue(), creationDate, accessToken)).thenReturn(pagedModelIngestionFlowFile);

        // When
        List<IngestionFlowFile> result = ingestionFlowFileService.findByOrganizationIdFlowTypeCreateDate(organizationId, flowFileType, creationDate);

        // Then
        assertEquals(expectedResponse, result);
        verify(ingestionFlowFileClientMock, times(1)).findByOrganizationIDFlowTypeCreateDate(organizationId, flowFileType.getValue(), creationDate, accessToken);
    }
}