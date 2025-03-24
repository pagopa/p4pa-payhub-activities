package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class FinalizeDebtPositionSyncStatusActivityTest {

    @Mock
    private DebtPositionService debtPositionServiceMock;

    private FinalizeDebtPositionSyncStatusActivity activity;

    @BeforeEach
    void init() {
        activity = new FinalizeDebtPositionSyncStatusActivityImpl(debtPositionServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(debtPositionServiceMock);
    }

    @Test
    void givenFinalizeSyncStatusThenSuccess(){
        // Given
        DebtPositionDTO debtPosition = buildDebtPositionDTO();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = IupdSyncStatusUpdateDTO.builder()
                .newStatus(InstallmentStatus.TO_SYNC)
                .build();

        Mockito.when(debtPositionServiceMock.finalizeSyncStatus(1L, Map.of("iud", iupdSyncStatusUpdateDTO))).thenReturn(debtPosition);
        // When
        DebtPositionDTO result = activity.finalizeDebtPositionSyncStatus(1L, Map.of("iud", iupdSyncStatusUpdateDTO));

        // Then
        Mockito.verify(debtPositionServiceMock, Mockito.times(1))
                .finalizeSyncStatus(1L, Map.of("iud", iupdSyncStatusUpdateDTO));
        assertSame(result, debtPosition);
    }
}
