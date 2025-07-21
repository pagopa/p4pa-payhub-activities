package it.gov.pagopa.payhub.activities.connector.pu_sil;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pu_sil.client.PuSilClient;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.springframework.stereotype.Service;

@Service
public class PuSilServiceImpl implements PuSilService {

  private final PuSilClient puSilClient;
  private final AuthnService authnService;

  public PuSilServiceImpl(PuSilClient puSilClient, AuthnService authnService) {
    this.puSilClient = puSilClient;
    this.authnService = authnService;
  }

  @Override
  public void notifyPayment(Long orgSilServiceId, InstallmentDTO installmentDTO, String ipaCode) {
    puSilClient.notifyPayment(orgSilServiceId, installmentDTO, authnService.getAccessToken(ipaCode));
  }
}
