package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.PaymentsReportingPagoPaClient;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingPagoPaServiceTest {
	@Mock
	private PaymentsReportingPagoPaClient paymentsReportingPagoPaClientMock;
	@Mock
	private AuthnService authnServiceMock;

	private PaymentsReportingPagoPaService service;

	@BeforeEach
	void setUp() { service = new PaymentsReportingPagoPaServiceImpl(paymentsReportingPagoPaClientMock, authnServiceMock); }

	@AfterEach
	void tearDown() {
		Mockito.verifyNoMoreInteractions(paymentsReportingPagoPaClientMock, authnServiceMock);
	}

	@Test
	void testGetPaymentsReportingList() {
		// Given
		String accessToken = "accessToken";
		Long organizationId = 1L;
		List<PaymentsReportingIdDTO> expectedResponse = List.of(new PaymentsReportingIdDTO());
		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(paymentsReportingPagoPaClientMock.getPaymentsReportingList(organizationId, accessToken)).thenReturn(expectedResponse);
		// When
		List<PaymentsReportingIdDTO> result = service.getPaymentsReportingList(organizationId);

		// Then
		assertEquals(expectedResponse, result);
		verify(authnServiceMock, Mockito.times(1)).getAccessToken();
	}

	@Test
	void testFetchPaymentReporting() {
		// Given
		String accessToken = "accessToken";
		Long organizationId = 1L;
		String flowId = "flowId";
		String expectedResponse = "response";

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(paymentsReportingPagoPaClientMock.fetchPaymentReporting(organizationId, flowId, accessToken)).thenReturn(expectedResponse);

		// When
		String result = service.fetchPaymentReporting(organizationId, flowId);

		// Then
		assertEquals(expectedResponse, result);
	}
}