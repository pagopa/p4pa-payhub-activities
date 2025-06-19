package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeEntityControllerApi;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeSearchControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelDebtPositionType;
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
    @Mock
    private DebtPositionTypeSearchControllerApi debtPositionTypeSearchControllerApiMock;

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

    @Test
    void whenFindByMainFieldsThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String code = "code";
        Long brokerId = 1L;
        String orgType = "orgType";
        String macroArea = "macroArea";
        String serviceType = "serviceType";
        String collectingReason = "collectingReason";
        String taxonomyCode = "taxonomyCode";
        CollectionModelDebtPositionType expectedResult = new CollectionModelDebtPositionType();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeSearchControllerApi(accessToken))
            .thenReturn(debtPositionTypeSearchControllerApiMock);
        Mockito.when(debtPositionTypeSearchControllerApiMock.crudDebtPositionTypesFindByMainFields(
                code, brokerId, orgType, macroArea, serviceType, collectingReason, taxonomyCode))
            .thenReturn(expectedResult);

        // When
        CollectionModelDebtPositionType result = client.getByMainFields(code, brokerId, orgType, macroArea, serviceType, collectingReason, taxonomyCode, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }


    @Test
    void whenFindByBrokerIdAndCodeThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String code = "code";
        Long brokerId = 1L;

        CollectionModelDebtPositionType expectedResult = new CollectionModelDebtPositionType();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeSearchControllerApi(accessToken))
            .thenReturn(debtPositionTypeSearchControllerApiMock);
        Mockito.when(debtPositionTypeSearchControllerApiMock.crudDebtPositionTypesFindByBrokerIdAndCode(
                        brokerId, code))
            .thenReturn(expectedResult);

        // When
        CollectionModelDebtPositionType result = client.getByBrokerIdAndCode(brokerId, code, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }


}
