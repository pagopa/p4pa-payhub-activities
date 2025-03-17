package it.gov.pagopa.payhub.activities.connector.workflowhub;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.client.WorkflowDebtPositionClient;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class WorkflowDebtPositionServiceTest {

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private WorkflowDebtPositionClient workflowDebtPositionClientMock;

    private WorkflowDebtPositionService service;

    @BeforeEach
    void setUp() {
        service = new WorkflowDebtPositionServiceImpl(authnServiceMock, workflowDebtPositionClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                workflowDebtPositionClientMock,
                authnServiceMock);
    }

    @Test
    void givenGetWorkflowStatusThenOk(){
        String token = "token";
        WorkflowCreatedDTO workflowCreatedDTO = WorkflowCreatedDTO.builder()
                .workflowId("workflowId")
                .build();
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(token);

        Mockito.when(workflowDebtPositionClientMock.syncDebtPosition(debtPositionDTO, false, PaymentEventType.DP_CREATED, token))
                .thenReturn(workflowCreatedDTO);

        WorkflowCreatedDTO result = service.syncDebtPosition(debtPositionDTO, false, PaymentEventType.DP_CREATED);

        assertEquals(result, workflowCreatedDTO);
    }
}
