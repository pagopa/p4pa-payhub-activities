package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.PaymentsReportingPagoPaClient;
import it.gov.pagopa.payhub.activities.util.faker.OrganizationFaker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
		Organization organization = OrganizationFaker.buildOrganizationDTO();
		String flowId = "flowId";
		String fileName = "fileName";
		Long revision = 1L;
		String pspId = "pspId";
		Long expectedResponse = 123L;

		when(authnServiceMock.getAccessToken(organization.getIpaCode())).thenReturn(accessToken);
		when(paymentsReportingPagoPaClientMock.fetchPaymentReporting(organization.getOrganizationId(), flowId, fileName, revision, pspId, accessToken)).thenReturn(expectedResponse);

		// When
		Long result = service.fetchPaymentReporting(organization, flowId, fileName, revision, pspId);

		// Then
		assertEquals(expectedResponse, result);
	}
}