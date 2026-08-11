package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgRequestBody;
import it.gov.pagopa.pu.debtpositions.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgServiceTest {

    @Mock
    private DebtPositionTypeOrgClient debtPositionTypeOrgClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private DebtPositionTypeOrgService debtPositionTypeOrgService;

    private final String accessToken = "ACCESSTOKEN";


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
        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        debtPositionTypeOrgService.getDefaultIONotificationDetails(1L, PaymentEventType.DP_CREATED);

        // Then
        verify(debtPositionTypeOrgClientMock).getIONotificationDetails(1L, PaymentEventType.DP_CREATED, accessToken);
    }

    @Test
    void givenGetDefaultIONotificationDetailsWhenOperationTypeDPUpdatedThenInvokeClient() {
        // When
        IONotificationDTO ioNotificationDetails = debtPositionTypeOrgService.getDefaultIONotificationDetails(1L, PaymentEventType.DP_UPDATED);

        // Then
        verify(debtPositionTypeOrgClientMock, times(0)).getIONotificationDetails(1L, PaymentEventType.DP_UPDATED, accessToken);
        assertNull(ioNotificationDetails);
    }

    @Test
    void whenGetDefaultIONotificationDetailsThrowsExceptionThenReturnNull() {
        // Given
        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        when(debtPositionTypeOrgClientMock.getIONotificationDetails(1L, PaymentEventType.DP_CREATED, accessToken))
                .thenThrow(new RuntimeException("API error"));

        // When
        IONotificationDTO result = debtPositionTypeOrgService.getDefaultIONotificationDetails(1L, PaymentEventType.DP_CREATED);

        // Then
        assertNull(result);
        verify(debtPositionTypeOrgClientMock)
                .getIONotificationDetails(1L, PaymentEventType.DP_CREATED, accessToken);
    }

    @Test
    void whenGetByIdThenInvokeClient() {
        // Given
        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        debtPositionTypeOrgService.getById(1L);

        // Then
        verify(debtPositionTypeOrgClientMock).findById(1L, accessToken);
    }

    @Test
    void whenGetDebtPositionTypeOrgByInstallmentIdThenInvokeClient() {
        // Given
        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        debtPositionTypeOrgService.getDebtPositionTypeOrgByInstallmentId(1L);

        // Then
        verify(debtPositionTypeOrgClientMock).getDebtPositionTypeOrgByInstallmentId(1L, accessToken);
    }

    @Test
    void whenGetDebtPositionTypeOrgByOrganizationIdAndCodeThenInvokeClient() {
        // Given
        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        debtPositionTypeOrgService.getDebtPositionTypeOrgByOrganizationIdAndCode(1L, "CODE");

        // Then
        verify(debtPositionTypeOrgClientMock).getDebtPositionTypeOrgByOrganizationIdAndCode(1L, "CODE", accessToken);
    }

    @Test
    void givenDebtPositionTypeOrgRequestBodyWhenCreateDebtPositionTypeOrgThenReturnDebtPositionTypeOrgType() {
        // Given
        DebtPositionTypeOrgRequestBody requestBody = new DebtPositionTypeOrgRequestBody();
        DebtPositionTypeOrg expectedDebtPositionTypeOrg = new DebtPositionTypeOrg();
        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        when(debtPositionTypeOrgClientMock.createDebtPositionTypeOrg(requestBody, accessToken))
                .thenReturn(expectedDebtPositionTypeOrg);

        // When
        DebtPositionTypeOrg result = debtPositionTypeOrgService.createDebtPositionTypeOrg(requestBody);

        // Then
        Assertions.assertSame(expectedDebtPositionTypeOrg, result);
    }

}
