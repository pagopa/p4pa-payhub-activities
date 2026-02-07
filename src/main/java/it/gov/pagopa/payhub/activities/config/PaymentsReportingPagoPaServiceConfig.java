package it.gov.pagopa.payhub.activities.config;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaRestServiceImpl;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaSoapServiceImpl;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.PaymentsReportingPagoPaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentsReportingPagoPaServiceConfig {

	private final boolean isLegacyFeatureActive;

	public PaymentsReportingPagoPaServiceConfig(
			@Value("${ingestion-flow-files.payments-reporting.legacy-feature-flags}") boolean legacyFeatureFlag) {
		this.isLegacyFeatureActive = legacyFeatureFlag;
	}

	@Bean
	public PaymentsReportingPagoPaService paymentsReportingPagoPaService(
			PaymentsReportingPagoPaClient paymentsReportingPagoPaClient,
			AuthnService authnService) {
		if (isLegacyFeatureActive) {
			return new PaymentsReportingPagoPaSoapServiceImpl(
					paymentsReportingPagoPaClient,
					authnService
			);
		}
		return new PaymentsReportingPagoPaRestServiceImpl(
				paymentsReportingPagoPaClient,
				authnService
		);
	}

}
