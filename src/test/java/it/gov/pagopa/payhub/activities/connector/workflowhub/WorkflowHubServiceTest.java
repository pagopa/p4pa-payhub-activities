package it.gov.pagopa.payhub.activities.connector.workflowhub;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.client.WorkflowHubClient;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class WorkflowHubServiceTest {

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private WorkflowHubClient debtPositionClientMock;

    private WorkflowHubService service;

    @BeforeEach
    void setUp() {
        service = new WorkflowHubServiceImpl(authnServiceMock, debtPositionClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionClientMock,
                authnServiceMock);
    }

    @Test
    void givenGetWorkflowStatusThenOk() {
        String token = "token";
        String workflowId = "workflowId";
        WorkflowStatusDTO wfStatus = WorkflowStatusDTO.builder()
                .workflowId(workflowId)
                .workflowType("WFTYPE")
                .runId("RUNID")
                .taskQueue("TASKQUEUE")
                .startDateTime(OffsetDateTime.now())
                .executionDateTime(OffsetDateTime.now().plusMinutes(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .duration("PT0S")
                .status(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED.name())
                .build();

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(token);

        Mockito.when(debtPositionClientMock.getWorkflowStatus(token, workflowId))
                .thenReturn(wfStatus);

        WorkflowStatusDTO workflowStatusDTO = service.getWorkflowStatus(workflowId);

        assertEquals(workflowId, workflowStatusDTO.getWorkflowId());
        assertEquals(wfStatus.getStatus(), workflowStatusDTO.getStatus());
    }
}
