package it.gov.pagopa.payhub.activities.config;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaRestServiceImpl;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaSoapServiceImpl;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.PaymentsReportingPagoPaClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingPagoPaServiceConfigTest {

	@Mock
	private PaymentsReportingPagoPaClient paymentsReportingPagoPaClient;
	@Mock
	private AuthnService authnService;

	private PaymentsReportingPagoPaServiceConfig paymentsReportingPagoPaServiceConfig;

	@Test
	void LegacyTest() {
		Boolean isLegacyFeatureActive = Boolean.TRUE;
		paymentsReportingPagoPaServiceConfig =
				new PaymentsReportingPagoPaServiceConfig(isLegacyFeatureActive);

		PaymentsReportingPagoPaService paymentsReportingPagoPaService = paymentsReportingPagoPaServiceConfig
				.paymentsReportingPagoPaService(paymentsReportingPagoPaClient, authnService);
		assertInstanceOf(
				PaymentsReportingPagoPaSoapServiceImpl.class,
				paymentsReportingPagoPaService
		);
	}

	@Test
	void NonLegacyTest() {
		Boolean isLegacyFeatureActive = Boolean.FALSE;
		paymentsReportingPagoPaServiceConfig =
				new PaymentsReportingPagoPaServiceConfig(isLegacyFeatureActive);

		PaymentsReportingPagoPaService paymentsReportingPagoPaService = paymentsReportingPagoPaServiceConfig
				.paymentsReportingPagoPaService(paymentsReportingPagoPaClient, authnService);
		assertInstanceOf(
				PaymentsReportingPagoPaRestServiceImpl.class,
				paymentsReportingPagoPaService
		);
	}

}
