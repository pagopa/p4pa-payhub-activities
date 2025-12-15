package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.dto.classifications.DuplicatePaymentReportingCheckActivityResult;
import it.gov.pagopa.payhub.activities.dto.classifications.DuplicatePaymentsReportingQueryDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
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

  public DuplicatePaymentReportingCheckActivityImpl(ClassificationService classificationService) {
    this.classificationService = classificationService;
  }

  @Override
  public DuplicatePaymentReportingCheckActivityResult duplicateCheck(DuplicatePaymentsReportingQueryDTO queryFields) {
    // Delete Classification by queryFields and label = DOPPI
    // TODO: amount here is Classification's amount
    // classificationService.deleteDuplicates(queryFields.getOrgId(), queryFields.getIuv(), queryFields.getTransferIndex(), queryFields.getAmount(), ClassificationsEnum.DOPPI);

    // Find Payments Reporting by queryFields
    // amount here is PaymentsReporting amountPaidCents
    List<PaymentsReporting> paymentsReportingList = new ArrayList<>(); // TODO: instantiate with query
    // paymentsReportingService.findBy(queryFields.getOrgId(), queryFields.getIuv(), queryFields.getTransferIndex(), queryFields.getAmount())

    // If multiple Payments Reporting (different IURs) are found, create a Classification with label DOPPI for each
    List<String> iurs = paymentsReportingList.stream().map(PaymentsReporting::getIur).distinct().toList();
    if (iurs.size() < 1) {
      List<Classification> classifications = new ArrayList<>();

      paymentsReportingList.stream()
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
    }
    return null;
  }
}
