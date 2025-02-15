package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.PaymentsReportingEntityExtendedControllerApi;
import it.gov.pagopa.pu.classification.client.generated.PaymentsReportingSearchControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

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
    void whenGetBySemanticKeyThenOk() {
        // Given
        Long orgId = 1L;
        String iuv = "IUV123";
        String iur = "IUR123";
        int transferIndex = 0;
        String accessToken = "accessToken";
        PaymentsReporting expectedResponse = new PaymentsReporting();

        when(classificationApisHolderMock.getPaymentsReportingSearchApi(accessToken))
                .thenReturn(paymentsReportingSearchControllerApiMock);
        when(paymentsReportingSearchControllerApiMock.crudPaymentsReportingFindBySemanticKey(orgId, iuv, iur, transferIndex))
                .thenReturn(expectedResponse);

        // When
        PaymentsReporting result = paymentsReportingClient.getBySemanticKey(orgId, iuv, iur, transferIndex, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(paymentsReportingSearchControllerApiMock, times(1))
                .crudPaymentsReportingFindBySemanticKey(orgId, iuv, iur, transferIndex);
    }

    @Test
    void givenNotExistentPaymentsReportingWhenGetBySemanticKeyThenNull() {
        // Given
        Long orgId = 1L;
        String iuv = "IUV123";
        String iur = "IUR123";
        int transferIndex = 0;
        String accessToken = "accessToken";

        when(classificationApisHolderMock.getPaymentsReportingSearchApi(accessToken))
                .thenReturn(paymentsReportingSearchControllerApiMock);
        when(paymentsReportingSearchControllerApiMock.crudPaymentsReportingFindBySemanticKey(orgId, iuv, iur, transferIndex))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        PaymentsReporting result = paymentsReportingClient.getBySemanticKey(orgId, iuv, iur, transferIndex, accessToken);

        // Then
        Assertions.assertNull(result);
    }
}