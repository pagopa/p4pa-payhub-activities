package it.gov.pagopa.payhub.activities.connector.pagopapayments.client;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.config.PagoPaPaymentsApisHolder;
import it.gov.pagopa.pu.pagopapayments.dto.generated.GeneratedNoticeMassiveFolderDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.NoticeRequestMassiveDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class PrintPaymentNoticeClient {
	private final PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder;

	public PrintPaymentNoticeClient(PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder) {
		this.pagoPaPaymentsApisHolder = pagoPaPaymentsApisHolder;
	}

	public GeneratedNoticeMassiveFolderDTO generateMassive(NoticeRequestMassiveDTO noticeRequestMassiveDTO, String accessToken) {
		return pagoPaPaymentsApisHolder.getPrintPaymentNoticeApi(accessToken).generateMassive(noticeRequestMassiveDTO);
	}

	public SignedUrlResultDTO getSignedUrl(Long organizationId, String pdfGeneratedId, String accessToken) {
		return pagoPaPaymentsApisHolder.getPrintPaymentNoticeApi(accessToken).getSignedUrl(organizationId, pdfGeneratedId);
	}
}
