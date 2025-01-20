package it.gov.pagopa.payhub.activities.connector.aca.client;

import it.gov.pagopa.payhub.activities.connector.aca.config.AcaApisHolder;
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
    private AcaApisHolder acaApisHolderMock;
    @Mock
    private AcaApi acaApiMock;

    private AcaClient acaClient;

    @BeforeEach
    void setUp() { acaClient = new AcaClient(acaApisHolderMock); }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                acaApisHolderMock
        );
    }

    @Test
    void whenCreateAcaDebtPositionThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionDTO debtPositionDTO = buildPaymentsDebtPositionDTO();

        Mockito.when(acaApisHolderMock.getAcaApi(accessToken))
                .thenReturn(acaApiMock);

        // When
        acaClient.createAcaDebtPosition(debtPositionDTO, accessToken);

        // Then
        Mockito.verify(acaApiMock)
                .createAca(debtPositionDTO);
    }

    @Test
    void whenDeleteAcaDebtPositionThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionDTO debtPositionDTO = buildPaymentsDebtPositionDTO();

        Mockito.when(acaApisHolderMock.getAcaApi(accessToken))
                .thenReturn(acaApiMock);

        // When
        acaClient.deleteAcaDebtPosition(debtPositionDTO, accessToken);

        // Then
        Mockito.verify(acaApiMock)
                .deleteAca(debtPositionDTO);
    }
}
