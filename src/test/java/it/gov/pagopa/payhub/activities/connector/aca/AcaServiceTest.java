package it.gov.pagopa.payhub.activities.connector.aca;

import it.gov.pagopa.payhub.activities.connector.aca.client.AcaClient;
import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
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
class AcaServiceTest {

    @Mock
    private AcaClient acaClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private AcaServiceImpl acaService;

    @BeforeEach
    void setUp() {
        acaService = new AcaServiceImpl(
                acaClientMock,
                authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                acaClientMock,
                authnServiceMock);
    }

    @Test
    void whenSyncInstallmentAcaThenInvokeClient() {
        // Given
        String iud = "IUD";
        DebtPositionDTO debtPosition = buildPaymentsDebtPositionDTO();
        String accessToken = "ACCESSTOKEN";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        acaService.syncInstallmentAca(iud, debtPosition);

        // Then
        Mockito.verify(acaClientMock).syncAca(iud, debtPosition, accessToken);
    }

}
