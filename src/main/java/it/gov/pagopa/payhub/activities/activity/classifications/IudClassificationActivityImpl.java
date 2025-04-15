package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.TransferService;
import it.gov.pagopa.payhub.activities.dto.classifications.IudClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPIIResponse;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferResponse;
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
  private static final List<InstallmentStatus> INSTALLMENT_STATUS_SET = List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);

  private final InstallmentService installmentService;
  private final TransferService transferService;

  public IudClassificationActivityImpl(InstallmentService installmentService,
      TransferService transferService) {
    this.installmentService = installmentService;
    this.transferService = transferService;
  }

  @Override
  public IudClassificationActivityResult classify(Long organizationId, String iud) {
    log.info("Starting IUD Classification for organization id {} and iud {}", organizationId,iud);

    CollectionModelInstallmentNoPII installment = installmentService.getInstallmentsByOrgIdAndIudAndStatus(organizationId, iud, INSTALLMENT_STATUS_SET);
    List<InstallmentNoPIIResponse> installmentsList = installment.getEmbedded().getInstallmentNoPIIs();
    if (installmentsList == null || installmentsList.isEmpty()) {
      log.info("No installments found for organization id {} and iud {}", organizationId,iud);
      return IudClassificationActivityResult.builder()
          .organizationId(organizationId)
          .transfers2classify(Collections.emptyList())
          .build();
    }

    List<Transfer2ClassifyDTO> transfers2classify = new ArrayList<>();


    installmentsList.forEach(installmentNoPII -> {
      log.info("InstallmentNoPII: {}", installmentNoPII);
      List<TransferResponse> transferList = transferService.findByInstallmentId(installmentNoPII.getInstallmentId()).getEmbedded().getTransfers();

      transferList.forEach(transferResponse -> {
        log.info("TransferResponse: {}", transferResponse);

        transfers2classify.add(Transfer2ClassifyDTO.builder()
            .iuv(installmentNoPII.getIuv())
            .iur(installmentNoPII.getIur())
            .transferIndex(transferResponse.getTransferIndex())
            .build());
      });
    });

    return IudClassificationActivityResult.builder()
        .organizationId(organizationId)
        .transfers2classify(transfers2classify)
        .build();
  }
}
