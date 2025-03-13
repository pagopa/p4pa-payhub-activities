package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.InstallmentNoPIIClient;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPIIResponse;
import java.util.List;

public class InstallmentNoPIIServiceImpl implements InstallmentNoPIIService{
  private final AuthnService authnService;
  private final InstallmentNoPIIClient installmentNoPIIClient;

  public InstallmentNoPIIServiceImpl(AuthnService authnService,
      InstallmentNoPIIClient installmentNoPIIClient) {
    this.authnService = authnService;
    this.installmentNoPIIClient = installmentNoPIIClient;
  }

  @Override
  public List<InstallmentNoPIIResponse> getByReceiptId(Long receiptId) {
    return installmentNoPIIClient.getByReceiptId(authnService.getAccessToken(), receiptId);
  }
}
