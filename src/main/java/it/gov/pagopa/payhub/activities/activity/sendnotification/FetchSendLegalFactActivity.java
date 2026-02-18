package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;

/**
 * Interface to download and archive SEND Legal Fact
 */
@ActivityInterface
public interface FetchSendLegalFactActivity {

	/**
	 * Download and archive SEND Legal Fact.
	 *
	 * @param notificationRequestId the ID of send notification request
	 * @param legalFactCategory the LegalFactCategoryDTO of the send legal fact
	 * @param legalFactId the ID of send legal fact
	 */
	@ActivityMethod
	void downloadAndArchiveSendLegalFact(String notificationRequestId, LegalFactCategoryDTO legalFactCategory, String legalFactId);

}
