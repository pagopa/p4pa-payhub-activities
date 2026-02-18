package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface to update last processed SEND stream eventId.
 */
@ActivityInterface
public interface UpdateLastProcessedStreamEventIdActivity {

	/**
	 * Update last processed SEND stream eventId.
	 *
	 * @param streamId the ID of send stream
	 * @param lastEventId the ID of last processed send event
	 */
	@ActivityMethod
	void updateLastProcessedStreamEventId(String streamId, String lastEventId);
}
