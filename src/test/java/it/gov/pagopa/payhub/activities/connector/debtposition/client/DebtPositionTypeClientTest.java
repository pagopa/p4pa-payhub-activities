package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeEntityControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeRequestBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeClientTest {

    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    private DebtPositionTypeEntityControllerApi debtPositionTypeEntityControllerApiMock;

    private DebtPositionTypeClient client;

    @BeforeEach
    void setUp() {
        client = new DebtPositionTypeClient(debtPositionApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                debtPositionApisHolderMock
        );
    }

    @Test
    void testCreateDebtPositionType() {
        // Given
        String accessToken = "accessToken";
        DebtPositionType expectedDebtPositionType = new DebtPositionType();
        DebtPositionTypeRequestBody requestBody = new DebtPositionTypeRequestBody();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeEntityControllerApi(accessToken))
            .thenReturn(debtPositionTypeEntityControllerApiMock);
        Mockito.when(debtPositionTypeEntityControllerApiMock.crudCreateDebtpositiontype(requestBody))
            .thenReturn(expectedDebtPositionType);

        // When
        DebtPositionType result = client.createDebtPositionType(requestBody, accessToken);

        // Then
        Assertions.assertSame(expectedDebtPositionType, result);
    }


}
