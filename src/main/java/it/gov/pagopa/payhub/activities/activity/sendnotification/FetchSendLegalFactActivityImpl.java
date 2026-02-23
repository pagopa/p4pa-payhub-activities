package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendStreamSkippedEventException;
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
			String errorMessage = "Bad request in downloadAndArchiveSendLegalFact for notificationRequestId %s, legal fact category %s and id %s: error message %s".formatted(notificationRequestId, legalFactCategory, legalFactId, e.getMessage());
			throw new SendStreamSkippedEventException("Skipped an error during execution of activity %s: %s".formatted(FetchSendLegalFactActivity.class.getSimpleName(), errorMessage));
		}
	}
}
