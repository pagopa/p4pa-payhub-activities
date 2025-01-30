package it.gov.pagopa.payhub.activities.connector.aca.client;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.AcaClient;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.config.PagoPaPaymentsApisHolder;
import it.gov.pagopa.pu.pagopapayments.client.generated.AcaApi;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildPaymentsDebtPositionDTO;

@ExtendWith(MockitoExtension.class)
class AcaClientTest {

    @Mock
    private PagoPaPaymentsApisHolder pagoPaPaymentsApisHolderMock;
    @Mock
    private AcaApi acaApiMock;

    private AcaClient acaClient;

    @BeforeEach
    void setUp() { acaClient = new AcaClient(pagoPaPaymentsApisHolderMock); }

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
        DebtPositionDTO debtPositionDTO = buildPaymentsDebtPositionDTO();

        Mockito.when(pagoPaPaymentsApisHolderMock.getAcaApi(accessToken))
                .thenReturn(acaApiMock);

        // When
        acaClient.syncAca(iud, debtPositionDTO, accessToken);

        // Then
        Mockito.verify(acaApiMock)
                .syncAca(iud, debtPositionDTO);
    }

}
