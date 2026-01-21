package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.PaymentsReportingPagoPaClient;
import it.gov.pagopa.payhub.activities.util.faker.OrganizationFaker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingPagoPaSoapServiceImplTest {

	@Mock
	private PaymentsReportingPagoPaClient paymentsReportingPagoPaClientMock;
	@Mock
	private AuthnService authnServiceMock;

	@InjectMocks
	private PaymentsReportingPagoPaSoapServiceImpl service;

	@Test
	void testGetPaymentsReportingList() {
		// Given
		String accessToken = "accessToken";
		Long organizationId = 1L;
		List<PaymentsReportingIdDTO> expectedResponse = List.of(new PaymentsReportingIdDTO());
		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(paymentsReportingPagoPaClientMock.soapGetPaymentsReportingList(organizationId, accessToken)).thenReturn(expectedResponse);
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
		Long revision = null; // soapFetchPaymentReporting does not need revision (only for compliance with PaymentsReportingPagoPaService)
		String pspId = null; // soapFetchPaymentReporting does not need pspId (only for compliance with PaymentsReportingPagoPaService)
		Long expectedResponse = 123L;

		when(authnServiceMock.getAccessToken(organization.getIpaCode())).thenReturn(accessToken);
		when(paymentsReportingPagoPaClientMock.soapFetchPaymentReporting(organization.getOrganizationId(), flowId, fileName, accessToken)).thenReturn(expectedResponse);

		// When
		Long result = service.fetchPaymentReporting(organization, flowId, fileName, revision, pspId);

		// Then
		assertEquals(expectedResponse, result);
	}
}