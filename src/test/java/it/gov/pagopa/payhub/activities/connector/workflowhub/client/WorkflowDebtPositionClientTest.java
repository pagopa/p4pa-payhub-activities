package it.gov.pagopa.payhub.activities.connector.workflowhub.client;

import it.gov.pagopa.payhub.activities.connector.workflowhub.config.WorkflowHubApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.workflowhub.controller.generated.DebtPositionApi;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;

@ExtendWith(MockitoExtension.class)
class WorkflowDebtPositionClientTest {

    @Mock
    private WorkflowHubApisHolder workflowHubApisHolderMock;
    @Mock
    private DebtPositionApi debtPositionApiMock;

    private WorkflowDebtPositionClient client;

    @BeforeEach
    void setUp() {
        client = new WorkflowDebtPositionClient(workflowHubApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                workflowHubApisHolderMock
        );
    }

    @Test
    void whenSyncDebtPositionThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        Mockito.when(workflowHubApisHolderMock.getDebtPositionApi(accessToken))
                .thenReturn(debtPositionApiMock);

        // When
        client.syncDebtPosition(debtPositionDTO, false, PaymentEventType.DP_CREATED, accessToken);

        // Then
        Mockito.verify(debtPositionApiMock)
                .syncDebtPosition(debtPositionDTO, false, PaymentEventType.DP_CREATED);
    }
}
