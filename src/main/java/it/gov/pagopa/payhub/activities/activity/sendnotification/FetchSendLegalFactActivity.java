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
	 * @param sendNotificationId the ID of send notification
	 * @param legalFactId the ID of send legal fact
	 */
	@ActivityMethod
	void downloadAndCacheSendLegalFact(String sendNotificationId, LegalFactCategoryDTO category, String legalFactId) throws IOException;

}
