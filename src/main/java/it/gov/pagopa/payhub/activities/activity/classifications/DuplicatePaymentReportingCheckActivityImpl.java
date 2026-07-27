package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
  public void duplicatePaymentsCheck(Long organizationId, Transfer2ClassifyDTO transfer2ClassifyDTO) {
    ReceiptNoPII receipt = receiptService.getByPaymentReceiptId(transfer2ClassifyDTO.getIur());

    // Delete Classifications
    classificationService.deleteDuplicates(organizationId, transfer2ClassifyDTO.getIuv(), transfer2ClassifyDTO.getTransferIndex());

    // Find possible duplicates Payments Reporting
    List<PaymentsReporting> paymentsReportingList = paymentsReportingService.findDuplicates(organizationId, transfer2ClassifyDTO.getIuv(), transfer2ClassifyDTO.getTransferIndex(),
        receipt.getOrgFiscalCode());

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
