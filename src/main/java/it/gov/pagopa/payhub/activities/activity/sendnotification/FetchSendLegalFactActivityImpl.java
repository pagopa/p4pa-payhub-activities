package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class FetchSendLegalFactActivityImpl implements FetchSendLegalFactActivity {

	private final SendService sendService;

	public FetchSendLegalFactActivityImpl(SendService sendService) {
		this.sendService = sendService;
	}

	@Override
	public void downloadAndArchiveSendLegalFact(String notificationRequestId, LegalFactCategoryDTO legalFactCategory, String legalFactId) {
		sendService.downloadAndArchiveSendLegalFact(notificationRequestId, legalFactCategory, legalFactId);
	}
}
