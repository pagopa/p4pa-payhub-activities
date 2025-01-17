package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.PaymentsReportingApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.client.generated.PaymentsReportingEntityExtendedControllerApi;
import it.gov.pagopa.pu.classification.client.generated.PaymentsReportingSearchControllerApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingClientTest {

    @Mock
    private PaymentsReportingApisHolder paymentsReportingApisHolder;

    @Mock
    private PaymentsReportingEntityExtendedControllerApi paymentsReportingEntityExtendedControllerApi;

    @Mock
    private PaymentsReportingSearchControllerApi paymentsReportingSearchControllerApi;

    private PaymentsReportingClient paymentsReportingClient;

    @BeforeEach
    void setUp() {
        paymentsReportingClient = new PaymentsReportingClient(paymentsReportingApisHolder);
    }

    @Test
    void testSaveAll() {
        // Given
        List<PaymentsReporting> dtos = List.of(new PaymentsReporting());
        String accessToken = "accessToken";
        Integer expectedResponse = 1;

        when(paymentsReportingApisHolder.getPaymentsReportingEntityExtendedControllerApi(accessToken))
                .thenReturn(paymentsReportingEntityExtendedControllerApi);
        when(paymentsReportingEntityExtendedControllerApi.saveAll1(dtos)).thenReturn(expectedResponse);

        // When
        Integer result = paymentsReportingClient.saveAll(dtos, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentsReportingEntityExtendedControllerApi, times(1)).saveAll1(dtos);
    }

    @Test
    void testGetByOrganizationIdAndIuf() {
        // Given
        Long organizationId = 1L;
        String iuf = "IUF123";
        String accessToken = "accessToken";
        CollectionModelPaymentsReporting expectedResponse = new CollectionModelPaymentsReporting();

        when(paymentsReportingApisHolder.getPaymentsReportingSearchApi(accessToken))
                .thenReturn(paymentsReportingSearchControllerApi);
        when(paymentsReportingSearchControllerApi.crudPaymentsReportingFindByOrganizationIdAndIuf(organizationId, iuf))
                .thenReturn(expectedResponse);

        // When
        CollectionModelPaymentsReporting result = paymentsReportingClient.getByOrganizationIdAndIuf(organizationId, iuf, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentsReportingSearchControllerApi, times(1))
                .crudPaymentsReportingFindByOrganizationIdAndIuf(organizationId, iuf);
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

        when(paymentsReportingApisHolder.getPaymentsReportingSearchApi(accessToken))
                .thenReturn(paymentsReportingSearchControllerApi);
        when(paymentsReportingSearchControllerApi.crudPaymentsReportingFindByOrganizationIdAndIuvAndIurAndTransferIndex(orgId, iuv, iur, transferIndex))
                .thenReturn(expectedResponse);

        // When
        CollectionModelPaymentsReporting result = paymentsReportingClient.getBySemanticKey(orgId, iuv, iur, transferIndex, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentsReportingSearchControllerApi, times(1))
                .crudPaymentsReportingFindByOrganizationIdAndIuvAndIurAndTransferIndex(orgId, iuv, iur, transferIndex);
    }
}