package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgServiceTest {

    @Mock
    private DebtPositionTypeOrgClient debtPositionTypeOrgClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private DebtPositionTypeOrgService debtPositionTypeOrgService;

    @BeforeEach
    void setUp() {
        debtPositionTypeOrgService = new DebtPositionTypeOrgServiceImpl(authnServiceMock, debtPositionTypeOrgClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionTypeOrgClientMock,
                authnServiceMock);
    }

    @Test
    void whenGetIONotificationDetailsThenInvokeClient() {
        // Given
        String accessToken = "ACCESSTOKEN";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        debtPositionTypeOrgService.getIONotificationDetails(1L, NotificationRequestDTO.OperationTypeEnum.CREATE_DP);

        // Then
        Mockito.verify(debtPositionTypeOrgClientMock).getIONotificationDetails(accessToken, 1L, NotificationRequestDTO.OperationTypeEnum.CREATE_DP);
    }
}
