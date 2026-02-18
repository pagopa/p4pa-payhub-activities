package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.ProgressResponseElementV25DTO;
import java.util.List;

/**
 * Interface for fetching send notification event from a stream.
 */
@ActivityInterface
public interface GetSendNotificationEventsFromStreamActivity {
	/**
	 * Fetch notification event from a stream.
	 *
	 * @param sendStreamId the ID of send notification stream
	 */
	@ActivityMethod
	List<ProgressResponseElementV25DTO> fetchSendNotificationEventsFromStream(Long organizationId, String sendStreamId);

}