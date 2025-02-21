package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link ReceiptPagopaNotifySilActivity} for sending notification to SIL of received receipt.
 * This class handles sending notification to SIL of received receipt.
 */
@Slf4j
@Lazy
@Component
public class ReceiptPagopaNotifySilActivityImpl implements ReceiptPagopaNotifySilActivity {

  @Override
  public void handleNotifySil(ReceiptDTO receiptDTO, InstallmentDTO installmentDTO) {
    //TODO the real implementation will be added in the future
  }
}
