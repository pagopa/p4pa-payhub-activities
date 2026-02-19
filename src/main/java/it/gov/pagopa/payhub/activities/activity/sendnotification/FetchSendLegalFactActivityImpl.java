package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

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
		try {
			sendService.downloadAndArchiveSendLegalFact(notificationRequestId, legalFactCategory, legalFactId);
		} catch (HttpClientErrorException.BadRequest e) {
			throw new NotRetryableActivityException(
				"Bad request in downloadAndArchiveSendLegalFact for notificationRequestId %s, legal fact category %s and id %s: error message %s".formatted(notificationRequestId, legalFactCategory, legalFactId, e.getMessage()),
				e
			);
		}
	}
}
