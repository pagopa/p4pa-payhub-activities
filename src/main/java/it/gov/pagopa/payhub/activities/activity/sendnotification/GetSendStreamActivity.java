package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendStreamDTO;

/**
 * Interface for fetching SEND stream.
 */
@ActivityInterface
public interface GetSendStreamActivity {
	/**
	 * Fetch SEND stream for a given streamId.
	 *
	 * @param sendStreamId the ID of the stream (required)
	 */
	@ActivityMethod
	SendStreamDTO fetchSendStream(String sendStreamId);
}
