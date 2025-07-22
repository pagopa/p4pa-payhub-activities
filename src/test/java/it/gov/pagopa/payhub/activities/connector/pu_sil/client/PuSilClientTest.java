package it.gov.pagopa.payhub.activities.connector.pu_sil.client;

import static org.junit.jupiter.api.Assertions.*;

import it.gov.pagopa.payhub.activities.connector.pu_sil.config.PuSilApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.pusil.controller.generated.NotifyPaymentApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PuSilClientTest {
  @Mock
  private PuSilApisHolder puSilApisHolderMock;
  @Mock
  private NotifyPaymentApi notifyPaymentApiMock;

  private PuSilClient puSilClient;

  @BeforeEach
  void setUp() { puSilClient = new PuSilClient(puSilApisHolderMock); }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(puSilApisHolderMock);
  }

  @Test
  void whenNotifyPaymentThenInvokeWithAccessToken() {
    // Given
    String accessToken = "ACCESSTOKEN";
    Long orgSilServiceId = 1L;
    InstallmentDTO installmentDTO = new InstallmentDTO();

    Mockito.when(puSilApisHolderMock.getNotifyPaymentApi(accessToken)).thenReturn(notifyPaymentApiMock);
    Mockito.doNothing().when(notifyPaymentApiMock).notifyPayment(orgSilServiceId, installmentDTO);
    // When Then
    assertDoesNotThrow(()-> puSilClient.notifyPayment(orgSilServiceId, installmentDTO, accessToken));
  }

}