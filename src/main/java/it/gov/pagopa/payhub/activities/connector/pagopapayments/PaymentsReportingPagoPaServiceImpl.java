package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.PaymentsReportingPagoPaClient;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@Slf4j
public class PaymentsReportingPagoPaServiceImpl implements PaymentsReportingPagoPaService {
	private final PaymentsReportingPagoPaClient paymentsReportingPagoPaClient;
	private final AuthnService authnService;

	public PaymentsReportingPagoPaServiceImpl(PaymentsReportingPagoPaClient paymentsReportingPagoPaClient, AuthnService authnService) {
		this.paymentsReportingPagoPaClient = paymentsReportingPagoPaClient;
		this.authnService = authnService;
	}

	@Override
	public List<PaymentsReportingIdDTO> getPaymentsReportingList(Long organizationId) {
		log.info("Getting payments reporting list for organizationId: {}", organizationId);
		return paymentsReportingPagoPaClient.getPaymentsReportingList(organizationId, authnService.getAccessToken());
	}

	@Override
	public Long fetchPaymentReporting(Organization organization, String pagopaPaymentsReportingId, String fileName) {
		log.info("Fetching payment reporting for organizationId: {} and pagopaPaymentsReportingId: {} asking to store it with name: {}", organization.getOrganizationId(), pagopaPaymentsReportingId, fileName);
		return paymentsReportingPagoPaClient.fetchPaymentReporting(organization.getOrganizationId(), pagopaPaymentsReportingId, fileName, authnService.getAccessToken(organization.getIpaCode()));
	}
}
