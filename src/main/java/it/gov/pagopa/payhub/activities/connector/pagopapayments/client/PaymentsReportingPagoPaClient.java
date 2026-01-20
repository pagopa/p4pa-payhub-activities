package it.gov.pagopa.payhub.activities.connector.pagopapayments.client;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.config.PagoPaPaymentsApisHolder;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Lazy
@Service
public class PaymentsReportingPagoPaClient {
	private final PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder;

	public PaymentsReportingPagoPaClient(PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder) {
		this.pagoPaPaymentsApisHolder = pagoPaPaymentsApisHolder;
	}

	public List<PaymentsReportingIdDTO> restGetPaymentsReportingList(Long organizationId, OffsetDateTime latestFlowDate, String accessToken) {
		return pagoPaPaymentsApisHolder.getPaymentsReportingApi(accessToken)
				.restGetPaymentsReportingList(organizationId, latestFlowDate);
	}

	public Long restFetchPaymentReporting(Long organizationId, String pagopaPaymentsReportingId, String fileName, Long revision, String pspId, String accessToken) {
		return pagoPaPaymentsApisHolder.getPaymentsReportingApi(accessToken)
				.restFetchPaymentReporting(organizationId, pagopaPaymentsReportingId, fileName, revision, pspId);
	}

	public List<PaymentsReportingIdDTO> soapGetPaymentsReportingList(Long organizationId, String accessToken) {
		return pagoPaPaymentsApisHolder.getPaymentsReportingApi(accessToken)
				.soapGetPaymentsReportingList(organizationId);
	}

	public Long soapFetchPaymentReporting(Long organizationId, String pagopaPaymentsReportingId, String fileName, String accessToken) {
		return pagoPaPaymentsApisHolder.getPaymentsReportingApi(accessToken)
				.soapFetchPaymentReporting(organizationId, pagopaPaymentsReportingId, fileName);
	}
}
