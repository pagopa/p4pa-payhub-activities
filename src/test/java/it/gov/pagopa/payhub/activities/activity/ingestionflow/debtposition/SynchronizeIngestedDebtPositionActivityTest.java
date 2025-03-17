package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowDebtPositionService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedDebtPositions;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
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
    private DebtPositionService debtPositionServiceMock;
    @Mock
    private WorkflowDebtPositionService workflowDebtPositionServiceMock;

    private static final Integer PAGE_SIZE = 2;
    private static final List<String> DEFAULT_ORDERING = List.of("debtPositionId,asc");

    private SynchronizeIngestedDebtPositionActivity activity;

    @BeforeEach
    void setUp() {
        activity = new SynchronizeIngestedDebtPositionActivityImpl(
                debtPositionServiceMock, workflowDebtPositionServiceMock,PAGE_SIZE
        );
    }

    @Test
    void testSynchronizeIngestedDebtPositionWithoutErrors(){
        Long ingestionFlowFileId = 1L;
        DebtPositionDTO debtPosition1 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition2 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition3 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition4 = buildDebtPositionDTO();

        PagedDebtPositions pagedDebtPositionsFirstPage = PagedDebtPositions.builder()
                .content(List.of(debtPosition1, debtPosition2))
                .size(2L)
                .totalPages(2L)
                .totalElements(4L)
                .number(0L)
                .build();

        PagedDebtPositions pagedDebtPositionsSecondPage = PagedDebtPositions.builder()
                .content(List.of(debtPosition3, debtPosition4))
                .size(2L)
                .totalPages(2L)
                .totalElements(4L)
                .number(1L)
                .build();

        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, 0, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsFirstPage);
        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, 1, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsSecondPage);
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(Mockito.any(DebtPositionDTO.class), Mockito.anyBoolean(), Mockito.any()))
                .thenReturn(WorkflowCreatedDTO.builder().build());

        String result = activity.synchronizeIngestedDebtPosition(ingestionFlowFileId);

        assertEquals("", result);
    }
}
