package it.gov.pagopa.payhub.activities.connector.pu_sil.client;

import it.gov.pagopa.payhub.activities.connector.pu_sil.config.PuSilApisHolder;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PuSilClient {

  private final PuSilApisHolder puSilApisHolder;

  public PuSilClient(PuSilApisHolder puSilApisHolder) {
    this.puSilApisHolder = puSilApisHolder;
  }

  public void notifyPayment(Long orgSilServiceId, InstallmentDTO installmentDTO, String accessToken) {
    puSilApisHolder.getNotifyPaymentApi(accessToken).notifyPayment(orgSilServiceId, installmentDTO);
  }
}
