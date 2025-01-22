package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.pu.processexecutions.client.generated.IngestionFlowFileEntityControllerApi;
import it.gov.pagopa.pu.processexecutions.client.generated.IngestionFlowFileEntityExtendedControllerApi;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileClientTest {

    @Mock
    private ProcessExecutionsApisHolder processExecutionsApisHolder;

    private IngestionFlowFileClient ingestionFlowFileClient;

    @BeforeEach
    void setUp() {
        ingestionFlowFileClient = new IngestionFlowFileClient(processExecutionsApisHolder);
    }
    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(processExecutionsApisHolder);
    }



    @Test
    void testFindById() {
        // Given
        Long ingestionFlowFileId = 1L;
        String ingestionFlowFileIdString = String.valueOf(ingestionFlowFileId);
        String accessToken = "accessToken";
        IngestionFlowFile expectedResponse = new IngestionFlowFile();
        IngestionFlowFileEntityControllerApi mockApi = mock(IngestionFlowFileEntityControllerApi.class);
        when(processExecutionsApisHolder.getIngestionFlowFileEntityControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudGetIngestionflowfile(ingestionFlowFileIdString)).thenReturn(expectedResponse);

        // When
        IngestionFlowFile result = ingestionFlowFileClient.findById(ingestionFlowFileId, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(processExecutionsApisHolder.getIngestionFlowFileEntityControllerApi(accessToken), times(1))
                .crudGetIngestionflowfile(ingestionFlowFileIdString);
    }

    @Test
    void testUpdateStatus() {
        // Given
        Long ingestionFlowFileId = 1L;
        String status = "status";
        String discardFileName = "discardFileName";
        String codError = "codError";
        String accessToken = "accessToken";
        Integer expectedResponse = 1;
        IngestionFlowFileEntityExtendedControllerApi mockApi = mock(IngestionFlowFileEntityExtendedControllerApi.class);
        when(processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.updateStatus(ingestionFlowFileId, status, codError, discardFileName)).thenReturn(expectedResponse);

        // When
        Integer result = ingestionFlowFileClient.updateStatus(ingestionFlowFileId, status, codError, discardFileName, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken), times(1))
                .updateStatus(ingestionFlowFileId, status, codError, discardFileName);
    }
}