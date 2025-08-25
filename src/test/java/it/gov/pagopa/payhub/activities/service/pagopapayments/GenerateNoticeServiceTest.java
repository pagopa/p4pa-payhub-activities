package it.gov.pagopa.payhub.activities.service.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.pagopapayments.dto.generated.GeneratedNoticeMassiveFolderDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.NoticeRequestMassiveDTO;
import org.junit.jupiter.api.AfterEach;
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
class GenerateNoticeServiceTest {

    @Mock
    private PrintPaymentNoticeService printPaymentNoticeServiceMock;
    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;

    private GenerateNoticeService generateNoticeService;


    @BeforeEach
    void setUp() {
        generateNoticeService = new GenerateNoticeService(
                printPaymentNoticeServiceMock, ingestionFlowFileServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                printPaymentNoticeServiceMock,
                ingestionFlowFileServiceMock
        );
    }


    @Test
    void whenGenerateNoticesThenOk() {
        Long ingestionFlowFileId = 1L;
        String folderId = "folderId";
        DebtPositionDTO debtPosition1 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition2 = buildDebtPositionDTO();
        debtPosition2.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.UNPAID);

        List<DebtPositionDTO> debtPositionsGenerateNotices = List.of(debtPosition1, debtPosition2);
        List<String> iuvListGenerateNotices = List.of("iuv", "iuv");
        long pdfGenerated = 2;

        String requestId = "PU_" + debtPosition1.getOrganizationId() + "_" + ingestionFlowFileId;

        NoticeRequestMassiveDTO requestMassive = NoticeRequestMassiveDTO.builder()
            .debtPositions(debtPositionsGenerateNotices)
            .iuvList(iuvListGenerateNotices)
            .requestId(requestId)
            .build();
        GeneratedNoticeMassiveFolderDTO responseFolder = GeneratedNoticeMassiveFolderDTO.builder()
            .folderId(folderId)
            .build();

        Mockito.when(printPaymentNoticeServiceMock.generateMassive(requestMassive))
            .thenReturn(responseFolder);
        Mockito.when(ingestionFlowFileServiceMock.updatePdfGenerated(ingestionFlowFileId, pdfGenerated, responseFolder.getFolderId()))
            .thenReturn(1);

        String result = generateNoticeService.generateNotices(ingestionFlowFileId, debtPositionsGenerateNotices, iuvListGenerateNotices);

        assertEquals(folderId, result);
        assertEquals(2, pdfGenerated);
    }
}
