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
	 * @param organizationId the ID of the organization owning the stream
	 * @param subUnitCode the code of the organization sub unit owning the stream, null if the stream is at organization level
	 * @param sendStreamId the ID of send notification stream
	 */
	@ActivityMethod
	List<ProgressResponseElementV28DTO> fetchSendNotificationEventsFromStream(Long organizationId, String subUnitCode, String sendStreamId);

}