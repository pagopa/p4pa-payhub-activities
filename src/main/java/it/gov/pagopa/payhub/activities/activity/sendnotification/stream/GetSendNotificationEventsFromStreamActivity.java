package it.gov.pagopa.payhub.activities.activity.sendnotification.stream;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.ProgressResponseElementV28DTO;

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
	List<ProgressResponseElementV28DTO> fetchSendNotificationEventsFromStream(Long organizationId, String sendStreamId);

}