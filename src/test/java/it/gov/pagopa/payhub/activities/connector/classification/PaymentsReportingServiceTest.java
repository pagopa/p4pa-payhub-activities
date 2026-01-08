package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.PaymentsReportingClient;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelPaymentsReportingEmbedded;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void testGetByTransferSemanticKey() {
        // Given
        Long orgId = 1L;
        String iuv = "IUV123";
        String iur = "IUR123";
        int transferIndex = 0;
        String accessToken = "accessToken";
        CollectionModelPaymentsReporting expectedResponse = CollectionModelPaymentsReporting.builder()
            .embedded(
                PagedModelPaymentsReportingEmbedded.builder()
                    .paymentsReportings(List.of(new PaymentsReporting()))
                    .build()
            )
                .build();
        TransferSemanticKeyDTO tSKDTO = new TransferSemanticKeyDTO(orgId, iuv, iur, transferIndex);

        when(paymentsReportingClientMock.getByTransferSemanticKey(orgId, iuv, iur, transferIndex, accessToken)).thenReturn(expectedResponse);
        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        PaymentsReporting result = paymentsReportingService.getByTransferSemanticKey(tSKDTO);

        // Then
        assertEquals(expectedResponse.getEmbedded().getPaymentsReportings().getFirst(), result);
        verify(paymentsReportingClientMock, times(1)).getByTransferSemanticKey(orgId, iuv, iur, transferIndex, accessToken);
    }

    @Test
    void testFindDuplicates() {
        // Given
        Long organizationId = 1L;
        String iuv = "IUV_SEARCH";
        int transferIndex = 1;
        String orgFiscalCode = "fiscalCode";
        String accessToken = "accessToken";

        PaymentsReporting pr2 = new PaymentsReporting();
        pr2.setIuv("Z_IUV");
        PaymentsReporting pr1 = new PaymentsReporting();
        pr1.setIuv("A_IUV");

        CollectionModelPaymentsReporting mockResponse = CollectionModelPaymentsReporting.builder()
                .embedded(PagedModelPaymentsReportingEmbedded.builder()
                        .paymentsReportings(List.of(pr2, pr1))
                        .build())
                .build();

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(paymentsReportingClientMock.findDuplicates(organizationId, iuv, transferIndex, orgFiscalCode, accessToken))
                .thenReturn(mockResponse);

        // When
        List<PaymentsReporting> result = paymentsReportingService.findDuplicates(organizationId, iuv, transferIndex, orgFiscalCode);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("A_IUV", result.get(0).getIuv());
        assertEquals("Z_IUV", result.get(1).getIuv());

        verify(paymentsReportingClientMock, times(1)).findDuplicates(organizationId, iuv, transferIndex, orgFiscalCode, accessToken);
        verify(authnServiceMock, times(1)).getAccessToken();
    }
}