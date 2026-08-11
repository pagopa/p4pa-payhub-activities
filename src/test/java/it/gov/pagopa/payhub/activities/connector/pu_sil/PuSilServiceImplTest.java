package it.gov.pagopa.payhub.activities.connector.pu_sil;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pu_sil.client.PuSilClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PuSilServiceImplTest {

  @Mock
  private PuSilClient puSilClientMock;
  @Mock
  private AuthnService authnServiceMock;

  private PuSilService puSilService;

  @BeforeEach
  void setUp() {
    puSilService = new PuSilServiceImpl(puSilClientMock, authnServiceMock);
  }

  @Test
  void whenNotifyPaymentThenInvokeClient(){
    Long orgSilServiceId = 1L;
    String accessToken = "access_token";
    String ipaCode = "IPACODE";
    InstallmentDTO installmentDTO = new InstallmentDTO();

    when(authnServiceMock.getAccessToken(ipaCode)).thenReturn(accessToken);

    assertDoesNotThrow(() -> puSilService.notifyPayment(orgSilServiceId, installmentDTO, ipaCode));

    verify(puSilClientMock).notifyPayment(orgSilServiceId, installmentDTO, accessToken);
  }
}