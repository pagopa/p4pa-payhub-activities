package it.gov.pagopa.payhub.activities.connector.pu_sil;


import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;

public interface PuSilService {
  void notifyPayment(Long orgSilServiceId, InstallmentDTO installmentDTO, String ipaCode);
}
