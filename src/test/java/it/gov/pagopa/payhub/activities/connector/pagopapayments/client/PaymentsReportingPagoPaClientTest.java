package it.gov.pagopa.payhub.activities.connector.pagopapayments.client;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.config.PagoPaPaymentsApisHolder;
import it.gov.pagopa.pu.pagopapayments.client.generated.PaymentsReportingApi;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingPagoPaClientTest {

	@Mock
	private PagoPaPaymentsApisHolder pagoPaPaymentsApisHolderMock;

	private PaymentsReportingPagoPaClient client;

	@BeforeEach
	void setUp() { client = new PaymentsReportingPagoPaClient(pagoPaPaymentsApisHolderMock); }

	@AfterEach
	void tearDown() { Mockito.verifyNoMoreInteractions(pagoPaPaymentsApisHolderMock); }

	@Test
	void testGetPaymentsReportingList() {
		// Given
		Long organizationId = 1L;
		String accessToken = "accessToken";
		PaymentsReportingIdDTO expectedResponse = new PaymentsReportingIdDTO();
		PaymentsReportingApi mockApi = mock(PaymentsReportingApi.class);
		when(pagoPaPaymentsApisHolderMock.getPaymentsReportingApi(accessToken)).thenReturn(mockApi);
		when(mockApi.getPaymentsReportingList(organizationId)).thenReturn(List.of(expectedResponse));

		// When
		List<PaymentsReportingIdDTO> result = client.getPaymentsReportingList(organizationId, accessToken);

		// Then
		assertEquals(List.of(expectedResponse), result);
		verify(pagoPaPaymentsApisHolderMock.getPaymentsReportingApi(accessToken), times(1))
			.getPaymentsReportingList(organizationId);
	}

	@Test
	void testFetchPaymentReporting() {
		// Given
		Long organizationId = 1L;
		String flowId = "flowId";
		String fileName = "fileName";
		String accessToken = "accessToken";
		Long expectedResponse = 123L;

		PaymentsReportingApi mockApi = mock(PaymentsReportingApi.class);
		when(pagoPaPaymentsApisHolderMock.getPaymentsReportingApi(accessToken)).thenReturn(mockApi);
		when(mockApi.fetchPaymentReporting(organizationId, flowId, fileName)).thenReturn(expectedResponse);

		// When
		Long result = client.fetchPaymentReporting(organizationId, flowId, fileName, accessToken);

		// Then
		assertEquals(expectedResponse, result);
	}
}