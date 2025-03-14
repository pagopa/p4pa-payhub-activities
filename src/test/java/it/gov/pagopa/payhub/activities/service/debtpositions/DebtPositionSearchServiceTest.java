package it.gov.pagopa.payhub.activities.service.debtpositions;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.service.debtposition.DebtPositionSearchService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedDebtPositions;
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
class DebtPositionSearchServiceTest {

    @Mock
    private DebtPositionService debtPositionServiceMock;

    private static final Integer PAGE_SIZE = 2;
    private DebtPositionSearchService debtPositionSearchService;

    @BeforeEach
    void setUp() {
        debtPositionSearchService = new DebtPositionSearchService(debtPositionServiceMock, PAGE_SIZE);
    }

    @Test
    void testGetAllDebtPositionsByIngestionFlowFileId(){
        Long ingestionFlowFileId = 1L;
        DebtPositionDTO debtPosition1 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition2 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition3 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition4 = buildDebtPositionDTO();
        List<DebtPositionDTO> expectedDebtPositions = List.of(debtPosition1, debtPosition2, debtPosition3, debtPosition4);

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

        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, 0, PAGE_SIZE, null))
                .thenReturn(pagedDebtPositionsFirstPage);
        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, 1, PAGE_SIZE, null))
                .thenReturn(pagedDebtPositionsSecondPage);


        List<DebtPositionDTO> result = debtPositionSearchService.getAllDebtPositionsByIngestionFlowFileId(ingestionFlowFileId);
        assertEquals(result, expectedDebtPositions);

    }

}
