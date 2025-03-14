package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.service.debtposition.DebtPositionSearchService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SynchronizeIngestedDebtPositionActivityTest {

    @Mock
    private DebtPositionSearchService debtPositionSearchServiceMock;

    private SynchronizeIngestedDebtPositionActivity activity;

    @BeforeEach
    void setUp() {
        activity = new SynchronizeIngestedDebtPositionActivityImpl(
                debtPositionSearchServiceMock
        );
    }

    @Test
    void testSynchronizeIngestedDebtPositionWithoutErrors(){
        Long ingestionFlowFileId = 1L;
        List<DebtPositionDTO> debtPositions = List.of(buildDebtPositionDTO(), buildDebtPositionDTO());

        Mockito.when(debtPositionSearchServiceMock.getAllDebtPositionsByIngestionFlowFileId(ingestionFlowFileId))
                .thenReturn(debtPositions);

        String result = activity.synchronizeIngestedDebtPosition(ingestionFlowFileId);

        assertEquals("", result);
    }

}
