package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptPagopaNotifySilActivityTest {

  @InjectMocks
  private ReceiptPagopaNotifySilActivityImpl receiptPagopaNotifySilActivity;


  @Test
  void givenValidReceiptAndInstallmentWhenHandleNotifySilThenOk() {
    // Given
    ReceiptDTO receiptDTO = new ReceiptDTO();
    InstallmentDTO installmentDTO = new InstallmentDTO();

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaNotifySilActivity.handleNotifySil(receiptDTO, installmentDTO));

    // Then

  }

}