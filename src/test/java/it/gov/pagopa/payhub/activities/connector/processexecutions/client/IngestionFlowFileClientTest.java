package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.pu.processexecutions.client.generated.IngestionFlowFileEntityControllerApi;
import it.gov.pagopa.pu.processexecutions.client.generated.IngestionFlowFileEntityExtendedControllerApi;
import it.gov.pagopa.pu.processexecutions.client.generated.IngestionFlowFileSearchControllerApi;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.PagedModelIngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.*;
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
        StatusEnum status = StatusEnum.COMPLETED;
        String discardFileName = "discardFileName";
        String codError = "codError";
        String accessToken = "accessToken";
        Integer expectedResponse = 1;
        IngestionFlowFileEntityExtendedControllerApi mockApi = mock(IngestionFlowFileEntityExtendedControllerApi.class);
        when(processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.updateStatus(ingestionFlowFileId, status.name(), codError, discardFileName)).thenReturn(expectedResponse);

        // When
        Integer result = ingestionFlowFileClient.updateStatus(ingestionFlowFileId, status, codError, discardFileName, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken), times(1))
                .updateStatus(ingestionFlowFileId, status.name(), codError, discardFileName);
    }

    @Test
    void testFindByOrganizationIDFlowTypeCreateDate() {
        // Given
        Long organizationId = 1L;
        FlowFileTypeEnum flowFileType = FlowFileTypeEnum.PAYMENTS_REPORTING;
        OffsetDateTime creationDate = OffsetDateTime.now().minusDays(1);
        String accessToken = "accessToken";
        IngestionFlowFileSearchControllerApi mockApi = mock(IngestionFlowFileSearchControllerApi.class);
        PagedModelIngestionFlowFile expectedResponse = new PagedModelIngestionFlowFile();
        when(processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), flowFileType.getValue(), creationDate, null, null, null, null, null, null)).thenReturn(expectedResponse);

        // When
        PagedModelIngestionFlowFile result = ingestionFlowFileClient.findByOrganizationIDFlowTypeCreateDate(organizationId, flowFileType, creationDate, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken), times(1))
                .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), flowFileType.getValue(), creationDate, null, null, null, null, null, null);
    }
}