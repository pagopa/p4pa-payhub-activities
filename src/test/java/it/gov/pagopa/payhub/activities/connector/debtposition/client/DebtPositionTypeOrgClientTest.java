package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.IONotificationDTOFaker.buildIONotificationDTO;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgClientTest {

    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    private DebtPositionTypeOrgApi debtPositionTypeOrgApiMock;

    private DebtPositionTypeOrgClient debtPositionTypeOrgClient;

    @BeforeEach
    void setUp() {
        debtPositionTypeOrgClient = new DebtPositionTypeOrgClient(debtPositionApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                debtPositionApisHolderMock
        );
    }

    @Test
    void whenFinalizeSyncStatusThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        IONotificationDTO expectedResult = buildIONotificationDTO();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgApi(accessToken))
                .thenReturn(debtPositionTypeOrgApiMock);

        Mockito.when(debtPositionTypeOrgApiMock.getIONotificationDetails(1L, "DP_CREATED"))
                .thenReturn(expectedResult);

        // When
        IONotificationDTO result = debtPositionTypeOrgClient.getIONotificationDetails(accessToken, 1L, PaymentEventType.DP_CREATED);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
