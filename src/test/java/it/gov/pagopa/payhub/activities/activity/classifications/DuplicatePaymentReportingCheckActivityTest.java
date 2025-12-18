package it.gov.pagopa.payhub.activities.activity.classifications;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.classifications.DuplicatePaymentsReportingCheckDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

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

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        classificationServiceMock,
        paymentsReportingServiceMock,
        receiptServiceMock
    );
  }

  @Test
  void givenDuplicatesPaymentsReportingWhenDuplicatePaymentsCheckThenSaveDOPPIClassifications() {
    DuplicatePaymentsReportingCheckDTO dto = DuplicatePaymentsReportingCheckDTO.builder()
        .orgId(1L)
        .iuv("IUV")
        .transferIndex(1)
        .build();

    ReceiptNoPII receipt = podamFactory.manufacturePojo(ReceiptNoPII.class);
    when(receiptServiceMock.getByPaymentReceiptId(anyString())).thenReturn(receipt);

    when(classificationServiceMock.deleteDuplicates(eq(dto.getOrgId()), eq(dto.getIuv()), eq(dto.getTransferIndex()), eq(receipt.getPaymentAmountCents()), eq(receipt.getOrgFiscalCode())))
        .thenReturn(1);

    PaymentsReporting paymentsReporting1 = podamFactory.manufacturePojo(PaymentsReporting.class);
    paymentsReporting1.setIur("IUR1");
    PaymentsReporting paymentsReporting2 = podamFactory.manufacturePojo(PaymentsReporting.class);
    paymentsReporting2.setIur("IUR2");
    when(paymentsReportingServiceMock.findDuplicates(eq(dto.getOrgId()), eq(dto.getIuv()), eq(dto.getTransferIndex()), eq(receipt.getOrgFiscalCode())))
        .thenReturn(List.of(paymentsReporting1, paymentsReporting2));

    duplicatePaymentReportingCheckActivity.duplicatePaymentsCheck(dto, "IUR");

    List<Classification> expectedClassifications = List.of(
        new Classification()
            .paymentsReportingId(paymentsReporting1.getPaymentsReportingId())
            .organizationId(paymentsReporting1.getOrganizationId())
            .iuv(paymentsReporting1.getIuv())
            .transferIndex(paymentsReporting1.getTransferIndex())
            .iur(paymentsReporting1.getIur())
            .label(ClassificationsEnum.DOPPI),
        new Classification()
            .paymentsReportingId(paymentsReporting2.getPaymentsReportingId())
            .organizationId(paymentsReporting2.getOrganizationId())
            .iuv(paymentsReporting2.getIuv())
            .transferIndex(paymentsReporting2.getTransferIndex())
            .iur(paymentsReporting2.getIur())
            .label(ClassificationsEnum.DOPPI)
    );
    verify(classificationServiceMock).saveAll(expectedClassifications);
  }

  @Test
  void givenNoDuplicatesWhenDuplicatePaymentsCheckThenDoNothing() {
    String iur = "IUR";

    DuplicatePaymentsReportingCheckDTO dto = DuplicatePaymentsReportingCheckDTO.builder()
        .orgId(1L)
        .iuv("IUV")
        .transferIndex(1)
        .build();

    ReceiptNoPII receipt = podamFactory.manufacturePojo(ReceiptNoPII.class);
    when(receiptServiceMock.getByPaymentReceiptId(anyString())).thenReturn(receipt);

    when(classificationServiceMock.deleteDuplicates(eq(dto.getOrgId()), eq(dto.getIuv()), eq(dto.getTransferIndex()), eq(receipt.getPaymentAmountCents()), eq(receipt.getOrgFiscalCode())))
        .thenReturn(1);

    PaymentsReporting paymentsReporting1 = podamFactory.manufacturePojo(PaymentsReporting.class);
    PaymentsReporting paymentsReporting2 = podamFactory.manufacturePojo(PaymentsReporting.class);
    paymentsReporting1.setIur(iur);
    paymentsReporting2.setIur(iur);
    when(paymentsReportingServiceMock.findDuplicates(eq(dto.getOrgId()), eq(dto.getIuv()), eq(dto.getTransferIndex()), eq(receipt.getOrgFiscalCode())))
        .thenReturn(List.of(paymentsReporting1, paymentsReporting2));

    duplicatePaymentReportingCheckActivity.duplicatePaymentsCheck(dto, iur);

    verify(classificationServiceMock, never()).saveAll(any());
  }

}