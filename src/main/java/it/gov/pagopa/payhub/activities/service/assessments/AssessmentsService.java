package it.gov.pagopa.payhub.activities.service.assessments;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentNoPIIService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPIIResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Slf4j
@Service
public class AssessmentsService {

  private final InstallmentNoPIIService installmentNoPIIService;


  public AssessmentsService(InstallmentNoPIIService installmentNoPIIService) {
    this.installmentNoPIIService = installmentNoPIIService;
  }

  public List<InstallmentNoPIIResponse> getInstallmentsByReceiptId(Long receiptId) {
    return installmentNoPIIService.getByReceiptId(receiptId);
  }

}
