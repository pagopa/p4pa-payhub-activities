package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.classifications.DuplicatePaymentsReportingCheckDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class DuplicatePaymentReportingCheckActivityImpl implements DuplicatePaymentReportingCheckActivity {

  private final ClassificationService classificationService;
  private final PaymentsReportingService paymentsReportingService;
  private final ReceiptService receiptService;

  public DuplicatePaymentReportingCheckActivityImpl(ClassificationService classificationService,
                                                    PaymentsReportingService paymentsReportingService,
                                                    ReceiptService receiptService) {
    this.classificationService = classificationService;
    this.paymentsReportingService = paymentsReportingService;
    this.receiptService = receiptService;
  }

  @Override
  public void duplicateCheck(DuplicatePaymentsReportingCheckDTO duplicatePaymentsReportingCheckDTO, String transferIur) {
    ReceiptNoPII receipt = receiptService.getByPaymentReceiptId(transferIur);
    duplicatePaymentsReportingCheckDTO.setAmount(receipt.getPaymentAmountCents());
    duplicatePaymentsReportingCheckDTO.setOrgFiscalCode(receipt.getOrgFiscalCode());

    // Delete Classifications
    classificationService.deleteDuplicates(duplicatePaymentsReportingCheckDTO.getOrgId(), duplicatePaymentsReportingCheckDTO.getIuv(), duplicatePaymentsReportingCheckDTO.getTransferIndex(), duplicatePaymentsReportingCheckDTO.getAmount(), duplicatePaymentsReportingCheckDTO.getOrgFiscalCode());

    // Find possible duplicates Payments Reporting
    List<PaymentsReporting> paymentsReportingList = paymentsReportingService.findDuplicates(duplicatePaymentsReportingCheckDTO.getOrgId(), duplicatePaymentsReportingCheckDTO.getIuv(), duplicatePaymentsReportingCheckDTO.getTransferIndex(),
        duplicatePaymentsReportingCheckDTO.getOrgFiscalCode());

    // If multiple Payments Reporting (different IURs) are found, create a Classification with label DOPPI for each
    List<String> iurs = paymentsReportingList.stream().map(PaymentsReporting::getIur).distinct().toList();
    if (iurs.size() > 1) {
      List<Classification> classifications = new ArrayList<>();

      paymentsReportingList
          .forEach(pr -> {
            Classification classification = new Classification()
                .paymentsReportingId(pr.getPaymentsReportingId())
                .organizationId(pr.getOrganizationId())
                .iuv(pr.getIuv())
                .transferIndex(pr.getTransferIndex())
                .iur(pr.getIur())
                .label(ClassificationsEnum.DOPPI);
            classifications.add(classification);
          });

      classificationService.saveAll(classifications);
    }
  }
}
