package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.PrintPaymentNoticeClient;
import it.gov.pagopa.pu.pagopapayments.dto.generated.GeneratedNoticeMassiveFolderDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.NoticeRequestMassiveDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class PrintPaymentNoticeServiceImpl implements PrintPaymentNoticeService {
	private final PrintPaymentNoticeClient printPaymentNoticeClient;
	private final AuthnService authnService;

	public PrintPaymentNoticeServiceImpl(PrintPaymentNoticeClient printPaymentNoticeClient, AuthnService authnService) {
		this.printPaymentNoticeClient = printPaymentNoticeClient;
		this.authnService = authnService;
	}

	@Override
	public GeneratedNoticeMassiveFolderDTO generateMassive(NoticeRequestMassiveDTO noticeRequestMassiveDTO) {
		log.info("Generate massive notices for requestId: {}", noticeRequestMassiveDTO.getRequestId());
		return printPaymentNoticeClient.generateMassive(noticeRequestMassiveDTO, authnService.getAccessToken());
	}

	@Override
	public SignedUrlResultDTO getSignedUrl(Long organizationId, String folderId) {
		log.info("Get signed url for organizationId: {} and folderId: {}", organizationId, folderId);
		return printPaymentNoticeClient.getSignedUrl(organizationId, folderId, authnService.getAccessToken());
	}
}
