package it.gov.pagopa.payhub.activities.activity.classifications;

import static it.gov.pagopa.payhub.activities.util.DebtPositionUtilities.INSTALLMENT_PAYED_STATUSES_LIST;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.TransferService;
import it.gov.pagopa.payhub.activities.dto.classifications.IudClassificationActivityResult;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class IudClassificationActivityImpl implements IudClassificationActivity{

  private final InstallmentService installmentService;
  private final TransferService transferService;
  private final ClassificationService classificationService;
  private final PaymentNotificationService paymentNotificationService;

  public IudClassificationActivityImpl(InstallmentService installmentService,
      TransferService transferService, ClassificationService classificationService,
      PaymentNotificationService paymentNotificationService) {
    this.installmentService = installmentService;
    this.transferService = transferService;
    this.classificationService = classificationService;
    this.paymentNotificationService = paymentNotificationService;
  }

  @Override
  public IudClassificationActivityResult classify(Long organizationId, String iud) {
    log.info("Starting IUD Classification for organization id {} and iud {}", organizationId,iud);

    CollectionModelInstallmentNoPII installment = installmentService.getInstallmentsByOrgIdAndIudAndStatus(organizationId, iud,
        INSTALLMENT_PAYED_STATUSES_LIST);
    List<InstallmentNoPII> installmentsList = installment.getEmbedded().getInstallmentNoPIIs();
    if (installmentsList == null || installmentsList.isEmpty()) {
      log.info("No installments found for organization id {} and iud {}", organizationId,iud);
      return IudClassificationActivityResult.builder()
          .organizationId(organizationId)
          .iud(iud)
          .transferIndexes(Collections.emptyList())
          .build();
    }

    List<Integer> transferIndex = new ArrayList<>();


    installmentsList.forEach(installmentNoPII -> {
      log.debug("InstallmentNoPII: {}", installmentNoPII);
      List<Transfer> transferList = transferService.findByInstallmentId(installmentNoPII.getInstallmentId()).getEmbedded().getTransfers();

      transferList.forEach(transfer -> {
        log.debug("Transfer: {}", transfer);
        transferIndex.add(transfer.getTransferIndex());
      });
    });

    if (transferIndex.isEmpty()) {
      log.debug("Saving installments found for organization id {} and iud: {}", organizationId, iud);
      saveClassification(organizationId, iud);
    }

    InstallmentNoPII firstInstallment = installmentsList.getFirst();

    return IudClassificationActivityResult.builder()
        .organizationId(organizationId)
        .iud(iud)
        .iuv(firstInstallment.getIuv())
        .iur(firstInstallment.getIur())
        .transferIndexes(transferIndex)
        .build();
  }
  /**
   * save classification data
   *
   * @param organizationId organization id
   * @param iud  flow unique identifier
   */
  private void saveClassification(Long organizationId, String iud) {
    log.debug("retrieving payment notification from organizationId {} and iud", organizationId, iud);
    PaymentNotificationNoPII paymentNotificationNoPII = paymentNotificationService.getByOrgIdAndIud(organizationId, iud);

    log.debug("Saving classification IUD_NO_RT for organizationId: {} - iud: {}", organizationId, iud);
    classificationService.save(Classification.builder()
        .organizationId(organizationId)
        .iud(iud)
        .label(ClassificationsEnum.IUD_NO_RT)
        .lastClassificationDate(LocalDate.now())
        .debtPositionTypeOrgCode(paymentNotificationNoPII.getDebtPositionTypeOrgCode())
        .iuv(paymentNotificationNoPII.getIuv())
        .payDate(paymentNotificationNoPII.getPaymentExecutionDate())
        .build());
  }
}
