package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.PaymentsReportingEntityExtendedControllerApi;
import it.gov.pagopa.pu.classification.client.generated.PaymentsReportingSearchControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
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
    private ClassificationApisHolder classificationApisHolderMock;

    @Mock
    private PaymentsReportingEntityExtendedControllerApi paymentsReportingEntityExtendedControllerApiMock;

    @Mock
    private PaymentsReportingSearchControllerApi paymentsReportingSearchControllerApiMock;

    private PaymentsReportingClient paymentsReportingClient;

    @BeforeEach
    void setUp() {
        paymentsReportingClient = new PaymentsReportingClient(classificationApisHolderMock);
    }

    @Test
    void testSaveAll() {
        // Given
        List<PaymentsReporting> dtos = List.of(new PaymentsReporting());
        String accessToken = "accessToken";
        Integer expectedResponse = 1;

        when(classificationApisHolderMock.getPaymentsReportingEntityExtendedControllerApi(accessToken))
                .thenReturn(paymentsReportingEntityExtendedControllerApiMock);
        when(paymentsReportingEntityExtendedControllerApiMock.saveAll1(dtos)).thenReturn(expectedResponse);

        // When
        Integer result = paymentsReportingClient.saveAll(dtos, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentsReportingEntityExtendedControllerApiMock, times(1)).saveAll1(dtos);
    }

    @Test
    void whenGetByOrganizationIdAndIufThenOk() {
        // Given
        Long organizationId = 1L;
        String iuf = "IUF123";
        String accessToken = "accessToken";
        CollectionModelPaymentsReporting expectedResponse = new CollectionModelPaymentsReporting();

        when(classificationApisHolderMock.getPaymentsReportingSearchApi(accessToken))
                .thenReturn(paymentsReportingSearchControllerApiMock);
        when(paymentsReportingSearchControllerApiMock.crudPaymentsReportingFindByOrganizationIdAndIuf(organizationId, iuf))
                .thenReturn(expectedResponse);

        // When
        CollectionModelPaymentsReporting result = paymentsReportingClient.getByOrganizationIdAndIuf(organizationId, iuf, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentsReportingSearchControllerApiMock, times(1))
                .crudPaymentsReportingFindByOrganizationIdAndIuf(organizationId, iuf);
    }

    @Test
    void whenGetByTransferSemanticKeyThenOk() {
        // Given
        Long orgId = 1L;
        String iuv = "IUV123";
        String iur = "IUR123";
        int transferIndex = 0;
        String accessToken = "accessToken";
        CollectionModelPaymentsReporting expectedResponse = new CollectionModelPaymentsReporting();

        when(classificationApisHolderMock.getPaymentsReportingSearchApi(accessToken))
                .thenReturn(paymentsReportingSearchControllerApiMock);
        when(paymentsReportingSearchControllerApiMock.crudPaymentsReportingFindByTransferSemanticKey(orgId, iuv, iur, transferIndex))
                .thenReturn(expectedResponse);

        // When
        CollectionModelPaymentsReporting result = paymentsReportingClient.getByTransferSemanticKey(orgId, iuv, iur, transferIndex, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentsReportingSearchControllerApiMock, times(1))
                .crudPaymentsReportingFindByTransferSemanticKey(orgId, iuv, iur, transferIndex);
    }

    @Test
    void testFindDuplicates() {
        // Given
        Long organizationId = 1L;
        String iuv = "IUV_TEST";
        int transferIndex = 2;
        String orgFiscalCode = "FISCAL_CODE_123";
        String accessToken = "accessToken";

        CollectionModelPaymentsReporting expectedResponse = new CollectionModelPaymentsReporting();

        when(classificationApisHolderMock.getPaymentsReportingSearchApi(accessToken))
                .thenReturn(paymentsReportingSearchControllerApiMock);
        when(paymentsReportingSearchControllerApiMock.crudPaymentsReportingFindDuplicates(organizationId, iuv, transferIndex, orgFiscalCode))
                .thenReturn(expectedResponse);

        // When
        CollectionModelPaymentsReporting result = paymentsReportingClient.findDuplicates(organizationId, iuv, transferIndex, orgFiscalCode, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock, times(1)).getPaymentsReportingSearchApi(accessToken);
        verify(paymentsReportingSearchControllerApiMock, times(1)).crudPaymentsReportingFindDuplicates(organizationId, iuv, transferIndex, orgFiscalCode);
    }
}