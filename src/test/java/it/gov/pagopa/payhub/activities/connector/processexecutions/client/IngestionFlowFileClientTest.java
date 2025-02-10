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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.*;
import java.util.List;

import static it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.FlowFileTypeEnum;
import static it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.StatusEnum;
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
    void whenFindByIdThenOk() {
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
    void whenUpdateStatusThenOk() {
        // Given
        Long ingestionFlowFileId = 1L;
        StatusEnum oldStatus = StatusEnum.PROCESSING;
        StatusEnum newStatus = StatusEnum.COMPLETED;
        String discardFileName = "discardFileName";
        String codError = "codError";
        String accessToken = "accessToken";
        Integer expectedResponse = 1;
        IngestionFlowFileEntityExtendedControllerApi mockApi = mock(IngestionFlowFileEntityExtendedControllerApi.class);
        when(processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.updateStatus(ingestionFlowFileId, oldStatus.name(), newStatus.name(), codError, discardFileName)).thenReturn(expectedResponse);

        // When
        Integer result = ingestionFlowFileClient.updateStatus(ingestionFlowFileId, oldStatus, newStatus, codError, discardFileName, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken), times(1))
                .updateStatus(ingestionFlowFileId, oldStatus.name(), newStatus.name(), codError, discardFileName);
    }

    @Test
    void givenNotFoundWhenUpdateStatusThenOk() {
        // Given
        Long ingestionFlowFileId = 1L;
        StatusEnum oldStatus = StatusEnum.PROCESSING;
        StatusEnum newStatus = StatusEnum.COMPLETED;
        String discardFileName = "discardFileName";
        String codError = "codError";
        String accessToken = "accessToken";
        IngestionFlowFileEntityExtendedControllerApi mockApi = mock(IngestionFlowFileEntityExtendedControllerApi.class);
        when(processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.updateStatus(ingestionFlowFileId, oldStatus.name(), newStatus.name(), codError, discardFileName))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        Integer result = ingestionFlowFileClient.updateStatus(ingestionFlowFileId, oldStatus, newStatus, codError, discardFileName, accessToken);

        // Then
        assertEquals(0, result);
        verify(processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken), times(1))
                .updateStatus(ingestionFlowFileId, oldStatus.name(), newStatus.name(), codError, discardFileName);
    }

    @Test
    void whenFindByOrganizationIDFlowTypeCreateDateThenOk() {
        // Given
        Long organizationId = 1L;
        FlowFileTypeEnum flowFileType = FlowFileTypeEnum.PAYMENTS_REPORTING;
        OffsetDateTime creationDate = OffsetDateTime.of(LocalDateTime.of(LocalDate.of(2025,1,1), LocalTime.MIDNIGHT), ZoneOffset.UTC);
        LocalDateTime exptectedCreationDateFrom = LocalDateTime.of(LocalDate.of(2025,1,1), LocalTime.of(1,0));
        String accessToken = "accessToken";
        IngestionFlowFileSearchControllerApi mockApi = mock(IngestionFlowFileSearchControllerApi.class);
        PagedModelIngestionFlowFile expectedResponse = new PagedModelIngestionFlowFile();
        when(processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), List.of(flowFileType.getValue()), exptectedCreationDateFrom, null, null, null, null, null, null, null)).thenReturn(expectedResponse);

        // When
        PagedModelIngestionFlowFile result = ingestionFlowFileClient.findByOrganizationIDFlowTypeCreateDate(organizationId, flowFileType, creationDate, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken), times(1))
                .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), List.of(flowFileType.getValue()), exptectedCreationDateFrom, null, null, null, null, null, null, null);
    }

    @Test
    void whenFindByOrganizationIDFlowTypeFilenameThenOk() {
        // Given
        Long organizationId = 1L;
        FlowFileTypeEnum flowFileType = FlowFileTypeEnum.PAYMENTS_REPORTING;
        String fileName = "fileName";
        String accessToken = "accessToken";
        IngestionFlowFileSearchControllerApi mockApi = mock(IngestionFlowFileSearchControllerApi.class);
        PagedModelIngestionFlowFile expectedResponse = new PagedModelIngestionFlowFile();
        when(processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), List.of(flowFileType.getValue()), null, null, null, fileName, null, null, null, null)).thenReturn(expectedResponse);

        // When
        PagedModelIngestionFlowFile result = ingestionFlowFileClient.findByOrganizationIDFlowTypeFilename(organizationId, flowFileType, fileName, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken), times(1))
            .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), List.of(flowFileType.getValue()), null, null, null, fileName, null, null, null, null);
    }
}