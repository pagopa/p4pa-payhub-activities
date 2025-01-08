package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionSearchControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPosition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtPositionSearchClientTest {
    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    private DebtPositionSearchControllerApi debtPositionSearchControllerApiMock;

    private DebtPositionSearchClient debtPositionSearchClient;

    @BeforeEach
    void setUp() {
        debtPositionSearchClient = new DebtPositionSearchClient(debtPositionApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                debtPositionApisHolderMock
        );
    }

    @Test
    void whenGetOperatorInfoThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionId = 0L;
        DebtPosition expectedResult = new DebtPosition();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionSearchControllerApi(accessToken))
                .thenReturn(debtPositionSearchControllerApiMock);
        Mockito.when(debtPositionSearchControllerApiMock.executeSearchDebtpositionGet(debtPositionId))
                .thenReturn(expectedResult);

        // When
        DebtPosition result = debtPositionSearchClient.findById(debtPositionId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
