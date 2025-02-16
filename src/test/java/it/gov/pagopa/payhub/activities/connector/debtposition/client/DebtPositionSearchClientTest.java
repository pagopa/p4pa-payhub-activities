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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

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
    void whenFindByIdThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionId = 0L;
        DebtPosition expectedResult = new DebtPosition();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionSearchControllerApi(accessToken))
                .thenReturn(debtPositionSearchControllerApiMock);
        Mockito.when(debtPositionSearchControllerApiMock.crudDebtPositionsFindOneWithAllDataByDebtPositionId(debtPositionId))
                .thenReturn(expectedResult);

        // When
        DebtPosition result = debtPositionSearchClient.findById(debtPositionId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentDebtPositionWhenFindByIdThenNull(){
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionId = 0L;

        Mockito.when(debtPositionApisHolderMock.getDebtPositionSearchControllerApi(accessToken))
                .thenReturn(debtPositionSearchControllerApiMock);
        Mockito.when(debtPositionSearchControllerApiMock.crudDebtPositionsFindOneWithAllDataByDebtPositionId(debtPositionId))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        DebtPosition result = debtPositionSearchClient.findById(debtPositionId, accessToken);

        // Then
        Assertions.assertNull(result);
    }
}
