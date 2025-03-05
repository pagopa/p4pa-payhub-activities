package it.gov.pagopa.payhub.activities.connector.pagopapayments.client;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.config.PagoPaPaymentsApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.pagopapayments.client.generated.GpdApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;

@ExtendWith(MockitoExtension.class)
class GpdClientTest {

    @Mock
    private PagoPaPaymentsApisHolder pagoPaPaymentsApisHolderMock;
    @Mock
    private GpdApi gpdApiMock;

    private GpdClient gpdClient;

    @BeforeEach
    void setUp() { gpdClient = new GpdClient(pagoPaPaymentsApisHolderMock); }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                pagoPaPaymentsApisHolderMock
        );
    }

    @Test
    void whenSyncAcaThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String iud = "IUD";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        Mockito.when(pagoPaPaymentsApisHolderMock.getGpdApi(accessToken))
                .thenReturn(gpdApiMock);

        // When
        gpdClient.syncGpd(iud, debtPositionDTO, accessToken);

        // Then
        Mockito.verify(gpdApiMock)
                .syncGpd(iud, debtPositionDTO);
    }
}
