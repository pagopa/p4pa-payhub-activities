package it.gov.pagopa.payhub.activities.connector.pu_sil;

import it.gov.pagopa.payhub.activities.connector.pu_sil.client.PuSilClient;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.springframework.stereotype.Service;

@Service
public class PuSilServiceImpl implements PuSilService {

  private final PuSilClient puSilClient;

  public PuSilServiceImpl(PuSilClient puSilClient) {
    this.puSilClient = puSilClient;
  }

  @Override
  public void notifyPayment(Long orgSilServiceId, InstallmentDTO installmentDTO, String accessToken) {
    puSilClient.notifyPayment(orgSilServiceId, installmentDTO, accessToken);
  }
}
