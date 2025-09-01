package it.gov.pagopa.payhub.activities.service.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.pagopapayments.dto.generated.GeneratedNoticeMassiveFolderDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.NoticeRequestMassiveDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@Slf4j
public class GenerateNoticeService {
    private final PrintPaymentNoticeService printPaymentNoticeService;
    private final IngestionFlowFileService ingestionFlowFileService;

    public GenerateNoticeService(PrintPaymentNoticeService printPaymentNoticeService, IngestionFlowFileService ingestionFlowFileService) {
        this.printPaymentNoticeService = printPaymentNoticeService;
        this.ingestionFlowFileService = ingestionFlowFileService;
    }

    public String generateNotices(Long ingestionFlowFileId, List<DebtPositionDTO> debtPositionsGenerateNotices, List<String> iuvListGenerateNotices) {
        String requestId = "PU_" + debtPositionsGenerateNotices.getFirst().getOrganizationId() + "_" + ingestionFlowFileId;
        log.info("Triggering notice generateNotices for {} debtPositions with requestId {}", debtPositionsGenerateNotices.size(), requestId);

        NoticeRequestMassiveDTO request = NoticeRequestMassiveDTO.builder()
                .debtPositions(debtPositionsGenerateNotices)
                .iuvList(iuvListGenerateNotices)
                .requestId(requestId)
                .build();
        GeneratedNoticeMassiveFolderDTO folderDTO = printPaymentNoticeService.generateMassive(request);

        ingestionFlowFileService.updatePdfGenerated(
                ingestionFlowFileId,
                (long) iuvListGenerateNotices.size(),
                folderDTO.getFolderId()
        );
        return folderDTO.getFolderId();
    }
}
