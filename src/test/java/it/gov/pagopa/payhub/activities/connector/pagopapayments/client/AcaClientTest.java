package it.gov.pagopa.payhub.activities.connector.pagopapayments.client;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.config.PagoPaPaymentsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.pagopapayments.client.generated.AcaApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        when(pagoPaPaymentsApisHolderMock.getAcaApi(accessToken))
                .thenReturn(acaApiMock);

        // When
        acaClient.syncAca(iud, debtPositionDTO, accessToken);

        // Then
        verify(acaApiMock)
                .syncAca(iud, debtPositionDTO, Boolean.FALSE);
    }

}
