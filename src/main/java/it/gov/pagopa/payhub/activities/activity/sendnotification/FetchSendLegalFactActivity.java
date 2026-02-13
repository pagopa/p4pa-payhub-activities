package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;

import java.io.IOException;

/**
 * Interface to delivery fetch SEND Legal Fact
 */
@ActivityInterface
public interface FetchSendLegalFactActivity {

	/**
	 * Fetch SEND Legal Fact.
	 *
	 * @param notificationRequestId the ID of send notification request
	 * @param category the LegalFactCategoryDTO of the send legal fact
	 * @param legalFactId the ID of send legal fact
	 */
	@ActivityMethod
	void downloadAndCacheSendLegalFact(String notificationRequestId, LegalFactCategoryDTO category, String legalFactId) throws IOException;

}
