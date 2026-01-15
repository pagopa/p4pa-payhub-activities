package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.PaymentsReportingPagoPaClient;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Lazy
@Service
@Slf4j
public class PaymentsReportingPagoPaServiceImpl implements PaymentsReportingPagoPaService {
	private final PaymentsReportingPagoPaClient paymentsReportingPagoPaClient;
	private final PaymentsReportingService paymentsReportingService;
	private final AuthnService authnService;

	public PaymentsReportingPagoPaServiceImpl(PaymentsReportingPagoPaClient paymentsReportingPagoPaClient, PaymentsReportingService paymentsReportingService, AuthnService authnService) {
		this.paymentsReportingPagoPaClient = paymentsReportingPagoPaClient;
		this.paymentsReportingService = paymentsReportingService;
		this.authnService = authnService;
	}

	@Override
	public List<PaymentsReportingIdDTO> getPaymentsReportingList(Long organizationId) {
		log.info("Getting payments reporting list for organizationId: {}", organizationId);
		OffsetDateTime latestFlowDate = paymentsReportingService.findLatestFlowDate(organizationId);
		return paymentsReportingPagoPaClient.getPaymentsReportingList(organizationId, latestFlowDate, authnService.getAccessToken());
	}

	@Override
	public Long fetchPaymentReporting(Organization organization, String pagopaPaymentsReportingId, String fileName, Long revision, String pspId) {
		log.info("Fetching payment reporting for organizationId: {} and pagopaPaymentsReportingId: {} and revision: {} and pspId: {} asking to store it with name: {}", organization.getOrganizationId(), pagopaPaymentsReportingId, fileName, revision, pspId);
		return paymentsReportingPagoPaClient.fetchPaymentReporting(organization.getOrganizationId(), pagopaPaymentsReportingId, fileName, revision, pspId, authnService.getAccessToken(organization.getIpaCode()));
	}

}
