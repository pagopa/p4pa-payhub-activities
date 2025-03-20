package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

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
    void whenGetDefaultIONotificationDetailsThenInvokeClient() {
        // Given
        String accessToken = "ACCESSTOKEN";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        debtPositionTypeOrgService.getDefaultIONotificationDetails(1L, PaymentEventType.DP_CREATED);

        // Then
        Mockito.verify(debtPositionTypeOrgClientMock).getIONotificationDetails(1L, PaymentEventType.DP_CREATED, accessToken);
    }

    @Test
    void givenGetDefaultIONotificationDetailsWhenOperationTypeDPUpdatedThenInvokeClient() {
        // Given
        String accessToken = "ACCESSTOKEN";

        // When
        IONotificationDTO ioNotificationDetails = debtPositionTypeOrgService.getDefaultIONotificationDetails(1L, PaymentEventType.DP_UPDATED);

        // Then
        Mockito.verify(debtPositionTypeOrgClientMock, Mockito.times(0)).getIONotificationDetails(1L, PaymentEventType.DP_UPDATED, accessToken);
        assertNull(ioNotificationDetails);
    }

    @Test
    void whenGetDefaultIONotificationDetailsThrowsExceptionThenReturnNull() {
        // Given
        String accessToken = "ACCESSTOKEN";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        Mockito.when(debtPositionTypeOrgClientMock.getIONotificationDetails(1L, PaymentEventType.DP_CREATED, accessToken))
                .thenThrow(new RuntimeException("API error"));

        // When
        IONotificationDTO result = debtPositionTypeOrgService.getDefaultIONotificationDetails(1L, PaymentEventType.DP_CREATED);

        // Then
        assertNull(result);
        Mockito.verify(debtPositionTypeOrgClientMock)
                .getIONotificationDetails(1L, PaymentEventType.DP_CREATED, accessToken);
    }

    @Test
    void whenGetByIdThenInvokeClient() {
        // Given
        String accessToken = "ACCESSTOKEN";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        debtPositionTypeOrgService.getById(1L);

        // Then
        Mockito.verify(debtPositionTypeOrgClientMock).findById(1L, accessToken);
    }
}
