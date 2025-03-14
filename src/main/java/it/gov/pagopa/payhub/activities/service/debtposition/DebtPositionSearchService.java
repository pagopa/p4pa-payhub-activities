package it.gov.pagopa.payhub.activities.service.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedDebtPositions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Lazy
@Slf4j
public class DebtPositionSearchService {

    private final DebtPositionService debtPositionService;
    private final Integer pageSize;

    public DebtPositionSearchService(DebtPositionService debtPositionService,
                                     @Value("${debt-position-pagination.page-size}") Integer pageSize) {
        this.debtPositionService = debtPositionService;
        this.pageSize = pageSize;
    }

    public List<DebtPositionDTO> getAllDebtPositionsByIngestionFlowFileId(Long ingestionFlowFileId){
        log.info("Retrieving all debt positions related to ingestion flow file id {}", ingestionFlowFileId);

        List<DebtPositionDTO> debtPositions = new ArrayList<>();

        int currentPage = 0;
        boolean hasMorePages = true;

        while (hasMorePages) {
            PagedDebtPositions pagedDebtPositions = debtPositionService.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId,
                    currentPage,
                    pageSize,
                    null);

            debtPositions.addAll(pagedDebtPositions.getContent());

            currentPage++;
            hasMorePages = currentPage < pagedDebtPositions.getTotalPages();
        }

        return debtPositions;
    }
}
