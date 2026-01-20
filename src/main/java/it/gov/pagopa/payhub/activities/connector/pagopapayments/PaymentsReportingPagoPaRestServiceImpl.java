package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.PaymentsReportingPagoPaClient;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Lazy
@Slf4j
public class PaymentsReportingPagoPaRestServiceImpl implements PaymentsReportingPagoPaService {
	private final PaymentsReportingPagoPaClient paymentsReportingPagoPaClient;
	private final AuthnService authnService;

	public PaymentsReportingPagoPaRestServiceImpl(PaymentsReportingPagoPaClient paymentsReportingPagoPaClient, AuthnService authnService) {
		this.paymentsReportingPagoPaClient = paymentsReportingPagoPaClient;
		this.authnService = authnService;
	}

	@Override
	public List<PaymentsReportingIdDTO> getPaymentsReportingList(Long organizationId) {
		log.info("Getting payments reporting list for organizationId: {}", organizationId);
		return paymentsReportingPagoPaClient.restGetPaymentsReportingList(organizationId, null, authnService.getAccessToken());
	}

	@Override
	public Long fetchPaymentReporting(Organization organization, String pagopaPaymentsReportingId, String fileName, Long revision, String pspId) {
		log.info("Fetching payment reporting for organizationId: {} and pagopaPaymentsReportingId: {} and revision: {} and pspId: {} asking to store it with name: {}", organization.getOrganizationId(), pagopaPaymentsReportingId, revision, pspId, fileName);
		return paymentsReportingPagoPaClient.restFetchPaymentReporting(organization.getOrganizationId(), pagopaPaymentsReportingId, fileName, revision, pspId, authnService.getAccessToken(organization.getIpaCode()));
	}
}
