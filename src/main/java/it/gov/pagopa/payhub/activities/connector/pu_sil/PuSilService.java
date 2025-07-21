package it.gov.pagopa.payhub.activities.connector.pu_sil;


import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;

public interface PuSilService {
  void notifyPayment(Long orgSilServiceId, InstallmentDTO installmentDTO, String accessToken);
}
