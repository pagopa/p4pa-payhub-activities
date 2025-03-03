package it.gov.pagopa.payhub.activities.connector.workflowhub;

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
    void givenGetWorkflowStatusThenOk(){
        String token = "token";
        String workflowId = "workflowId";
        String status = "status";

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(token);

        Mockito.when(debtPositionClientMock.getWorkflowStatus(token, workflowId))
                .thenReturn(new WorkflowStatusDTO(workflowId, status));

        WorkflowStatusDTO workflowStatusDTO = service.getWorkflowStatus(workflowId);

        assertEquals(workflowId, workflowStatusDTO.getWorkflowId());
        assertEquals(status, workflowStatusDTO.getStatus());
    }
}
