package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.SyncStatusUpdateRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Long debtPositionId = 1L;
        DebtPositionDTO debtPosition = buildDebtPositionDTO();
        SyncStatusUpdateRequestDTO requestDTO = new SyncStatusUpdateRequestDTO();

        Mockito.when(debtPositionServiceMock.finalizeSyncStatus(Mockito.same(debtPositionId), Mockito.same(requestDTO))).thenReturn(debtPosition);
        // When
        DebtPositionDTO result = activity.finalizeDebtPositionSyncStatus(debtPositionId, requestDTO);

        // Then
        assertSame(result, debtPosition);
    }
}
