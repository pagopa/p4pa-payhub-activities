package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionApi;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;

@ExtendWith(MockitoExtension.class)
class DebtPositionClientTest {

    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    private DebtPositionApi debtPositionApiMock;

    private DebtPositionClient debtPositionClient;

    @BeforeEach
    void setUp() {
        debtPositionClient = new DebtPositionClient(debtPositionApisHolderMock);
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
        Long debtPositionId = 0L;
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = IupdSyncStatusUpdateDTO.builder()
                .iupdPagopa("iudpPagopa")
                .newStatus(IupdSyncStatusUpdateDTO.NewStatusEnum.TO_SYNC)
                .build();
        DebtPositionDTO expectedResult = buildDebtPositionDTO();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
                .thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.finalizeSyncStatus(debtPositionId, Map.of("iud", iupdSyncStatusUpdateDTO) ))
                .thenReturn(expectedResult);

        // When
        DebtPositionDTO result = debtPositionClient.finalizeSyncStatus(accessToken, debtPositionId, Map.of("iud", iupdSyncStatusUpdateDTO) );

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
