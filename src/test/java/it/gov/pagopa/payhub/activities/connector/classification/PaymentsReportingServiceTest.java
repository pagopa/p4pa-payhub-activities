package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.PaymentsReportingClient;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingServiceTest {

    @Mock
    private PaymentsReportingClient paymentsReportingClientMock;
    @Mock
    private AuthnService authnServiceMock;
    private PaymentsReportingServiceImpl paymentsReportingService;

    @BeforeEach
    void setUp() {
        paymentsReportingService = new PaymentsReportingServiceImpl(paymentsReportingClientMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                paymentsReportingClientMock,
                authnServiceMock);
    }

    @Test
    void testSaveAll() {
        // Given
        List<PaymentsReporting> dtos = List.of(new PaymentsReporting());
        String accessToken = "accessToken";
        Integer expectedResponse = 1;

        when(paymentsReportingClientMock.saveAll(dtos, accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        Integer result = paymentsReportingService.saveAll(dtos);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentsReportingClientMock, times(1)).saveAll(dtos, accessToken);
    }

    @Test
    void testGetByOrganizationIdAndIuf() {
        // Given
        Long organizationId = 1L;
        String iuf = "IUF123";
        String accessToken = "accessToken";
        CollectionModelPaymentsReporting expectedResponse = new CollectionModelPaymentsReporting();

        when(paymentsReportingClientMock.getByOrganizationIdAndIuf(organizationId, iuf, accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        CollectionModelPaymentsReporting result = paymentsReportingService.getByOrganizationIdAndIuf(organizationId, iuf);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentsReportingClientMock, times(1)).getByOrganizationIdAndIuf(organizationId, iuf, accessToken);
    }

    @Test
    void testGetBySemanticKey() {
        // Given
        Long orgId = 1L;
        String iuv = "IUV123";
        String iur = "IUR123";
        int transferIndex = 0;
        String accessToken = "accessToken";
        CollectionModelPaymentsReporting expectedResponse = new CollectionModelPaymentsReporting();
        TransferSemanticKeyDTO tSKDTO = new TransferSemanticKeyDTO(orgId, iuv, iur, transferIndex);

        when(paymentsReportingClientMock.getBySemanticKey(orgId, iuv, iur, transferIndex, accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        CollectionModelPaymentsReporting result = paymentsReportingService.getBySemanticKey(tSKDTO);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentsReportingClientMock, times(1)).getBySemanticKey(orgId, iuv, iur, transferIndex, accessToken);
    }
}