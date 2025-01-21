package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.client.IngestionFlowFileClient;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
        String status = "status";
        String discardFileName = "discardFileName";
        Integer expectedResponse = 1;
        when(ingestionFlowFileClientMock.updateStatus(ingestionFlowFileId, status, discardFileName,accessToken)).thenReturn(expectedResponse);

        // When
        Integer result = ingestionFlowFileClientMock.updateStatus(ingestionFlowFileId, status, discardFileName,accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(ingestionFlowFileClientMock, times(1)).updateStatus(ingestionFlowFileId, status, discardFileName,accessToken);
    }
}