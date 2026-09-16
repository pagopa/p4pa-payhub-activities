package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptNoPII;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuplicatePaymentReportingCheckActivityTest {

    @InjectMocks
    private DuplicatePaymentReportingCheckActivityImpl duplicatePaymentReportingCheckActivity;

    @Mock
    private ClassificationService classificationServiceMock;
    @Mock
    private PaymentsReportingService paymentsReportingServiceMock;
    @Mock
    private ReceiptService receiptServiceMock;
    @Mock
    private TransferClassificationStoreService transferClassificationStoreServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                classificationServiceMock,
                paymentsReportingServiceMock,
                receiptServiceMock,
                transferClassificationStoreServiceMock
        );
    }

    @Test
    void givenDuplicatesPaymentsReportingWhenDuplicatePaymentsCheckThenSaveDOPPIClassifications() {
        String iuv = "IUV";
        String iur = "IUR";
        Transfer2ClassifyDTO dto = Transfer2ClassifyDTO.builder().iuv(iuv).transferIndex(1).iur(iur).build();
        Long orgId = 1L;

        ReceiptNoPII receipt = podamFactory.manufacturePojo(ReceiptNoPII.class);
        when(receiptServiceMock.getByPaymentReceiptId(dto.getIur())).thenReturn(receipt);

        when(classificationServiceMock.deleteDuplicates(orgId, dto.getIuv(), dto.getTransferIndex()))
                .thenReturn(1);

        PaymentsReporting paymentsReporting1 = podamFactory.manufacturePojo(PaymentsReporting.class);
        paymentsReporting1.setIur(iur);
        paymentsReporting1.setIuv(iuv);
        paymentsReporting1.setTransferIndex(1);

        TransferSemanticKeyDTO transferSemanticKeyDTO1 = TransferSemanticKeyDTO.builder()
                .orgId(orgId).iuv(iuv).iur(iur).transferIndex(1).build();

        PaymentsReporting paymentsReporting2 = podamFactory.manufacturePojo(PaymentsReporting.class);
        paymentsReporting2.setIur("IUR2");
        paymentsReporting2.setIuv(iuv);
        paymentsReporting2.setTransferIndex(1);

        TransferSemanticKeyDTO transferSemanticKeyDTO2 = TransferSemanticKeyDTO.builder()
                .orgId(orgId).iuv(iuv).iur("IUR2").transferIndex(1).build();

        when(paymentsReportingServiceMock.findDuplicates(orgId, dto.getIuv(), dto.getTransferIndex(), receipt.getOrgFiscalCode()))
                .thenReturn(List.of(paymentsReporting1, paymentsReporting2));

        doReturn(1).when(transferClassificationStoreServiceMock)
                .saveClassifications(transferSemanticKeyDTO1, null, null, paymentsReporting1, null, null, List.of(ClassificationsEnum.DOPPI));
        doReturn(1).when(transferClassificationStoreServiceMock)
                .saveClassifications(transferSemanticKeyDTO2, null, null, paymentsReporting2, null, null, List.of(ClassificationsEnum.DOPPI));

        assertDoesNotThrow(() -> duplicatePaymentReportingCheckActivity.duplicatePaymentsCheck(orgId, dto));
    }

    @Test
    void givenNoDuplicatesWhenDuplicatePaymentsCheckThenDoNothing() {
        String iur = "IUR";

        Transfer2ClassifyDTO dto = Transfer2ClassifyDTO.builder()
                .iuv("IUV")
                .transferIndex(1)
                .iur(iur)
                .build();
        Long orgId = 1L;

        ReceiptNoPII receipt = podamFactory.manufacturePojo(ReceiptNoPII.class);
        when(receiptServiceMock.getByPaymentReceiptId(anyString())).thenReturn(receipt);

        when(classificationServiceMock.deleteDuplicates(orgId, dto.getIuv(), dto.getTransferIndex()))
                .thenReturn(1);

        PaymentsReporting paymentsReporting1 = podamFactory.manufacturePojo(PaymentsReporting.class);
        PaymentsReporting paymentsReporting2 = podamFactory.manufacturePojo(PaymentsReporting.class);
        paymentsReporting1.setIur(iur);
        paymentsReporting2.setIur(iur);
        when(paymentsReportingServiceMock.findDuplicates(orgId, dto.getIuv(), dto.getTransferIndex(), receipt.getOrgFiscalCode()))
                .thenReturn(List.of(paymentsReporting1, paymentsReporting2));

        assertDoesNotThrow(() -> duplicatePaymentReportingCheckActivity.duplicatePaymentsCheck(orgId, dto));
    }

}