package it.gov.pagopa.payhub.activities.connector.pagopapayments.client;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.config.PagoPaPaymentsApisHolder;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
public class PaymentsReportingPagoPaClient {
	private final PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder;

	public PaymentsReportingPagoPaClient(PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder) {
		this.pagoPaPaymentsApisHolder = pagoPaPaymentsApisHolder;
	}

	public List<PaymentsReportingIdDTO> getPaymentsReportingList(Long organizationId, String accessToken) {
		return pagoPaPaymentsApisHolder.getPaymentsReportingApi(accessToken)
				.getPaymentsReportingList(organizationId, null); //TODO will be removed from OpenAPI payments-reporting in https://pagopa.atlassian.net/browse/P4ADEV-4297
	}

	public Long fetchPaymentReporting(Long organizationId, String pagopaPaymentsReportingId, String fileName, Long revision, String pspId, String accessToken) {
		return pagoPaPaymentsApisHolder.getPaymentsReportingApi(accessToken)
				.fetchPaymentReporting(organizationId, pagopaPaymentsReportingId, fileName, revision, pspId);
	}
}
