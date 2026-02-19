package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class UpdateLastProcessedStreamEventIdActivityImpl implements UpdateLastProcessedStreamEventIdActivity {

	private final SendNotificationService sendNotificationService;

	public UpdateLastProcessedStreamEventIdActivityImpl(SendNotificationService sendNotificationService) {
		this.sendNotificationService = sendNotificationService;
	}

	@Override
	public void updateLastProcessedStreamEventId(String streamId, String lastEventId) {
		log.info("Update SEND stream with ID {}, with last processed event ID {}", streamId, lastEventId);
		sendNotificationService.updateLastProcessedStreamEventId(streamId, lastEventId);
	}
}
