package it.gov.pagopa.payhub.activities.connector.pu_sil;

import static org.junit.jupiter.api.Assertions.*;

import it.gov.pagopa.payhub.activities.connector.pu_sil.client.PuSilClient;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PuSilServiceImplTest {

  @Mock
  private PuSilClient puSilClientMock;

  private PuSilService puSilService;

  @BeforeEach
  void setUp() {
    puSilService = new PuSilServiceImpl(puSilClientMock);
  }

  @Test
  void whenNotifyPaymentThenInvokeClient(){
    Long orgSilServiceId = 1L;
    String accessToken = "access_token";
    InstallmentDTO installmentDTO = new InstallmentDTO();

    assertDoesNotThrow(() -> puSilService.notifyPayment(orgSilServiceId, installmentDTO, accessToken));

    Mockito.verify(puSilClientMock).notifyPayment(orgSilServiceId, installmentDTO, accessToken);
  }
}