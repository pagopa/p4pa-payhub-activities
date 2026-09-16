package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.debtpositions.client.generated.DebtPositionEntityControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPosition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtPositionSearchClientTest {
    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    private DebtPositionEntityControllerApi debtPositionEntityControllerApiMock;

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

        when(debtPositionApisHolderMock.getDebtPositionEntityControllerApi(accessToken))
                .thenReturn(debtPositionEntityControllerApiMock);
        when(debtPositionEntityControllerApiMock.crudGetDebtposition(String.valueOf(debtPositionId)))
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

        when(debtPositionApisHolderMock.getDebtPositionEntityControllerApi(accessToken))
                .thenReturn(debtPositionEntityControllerApiMock);
        when(debtPositionEntityControllerApiMock.crudGetDebtposition(String.valueOf(debtPositionId)))
                .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        // When
        DebtPosition result = debtPositionSearchClient.findById(debtPositionId, accessToken);

        // Then
        Assertions.assertNull(result);
    }
}
