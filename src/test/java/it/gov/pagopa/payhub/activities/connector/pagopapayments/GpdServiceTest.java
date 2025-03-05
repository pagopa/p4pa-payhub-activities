package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.GpdClient;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GpdServiceTest {

    @Mock
    private GpdClient gpdClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private GpdServiceImpl gpdService;

    @BeforeEach
    void setUp() {
        gpdService = new GpdServiceImpl(
                gpdClientMock,
                authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                gpdClientMock,
                authnServiceMock);
    }

    @Test
    void whenSyncInstallmentGpdThenInvokeClient() {
        // Given
        String iud = "iud";
        DebtPositionDTO debtPosition = buildDebtPositionDTO();
        String accessToken = "ACCESSTOKEN";

        String iupdPagoPaExpected = "iupdPagopa";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        String result = gpdService.syncInstallmentGpd(iud, debtPosition);

        // Then
        assertEquals(iupdPagoPaExpected, result);
        Mockito.verify(gpdClientMock).syncGpd(iud, debtPosition, accessToken);
    }

}
