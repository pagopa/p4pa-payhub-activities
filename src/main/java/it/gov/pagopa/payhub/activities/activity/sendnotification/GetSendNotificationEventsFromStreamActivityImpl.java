package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.ProgressResponseElementV28DTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Lazy
@Slf4j
public class GetSendNotificationEventsFromStreamActivityImpl implements GetSendNotificationEventsFromStreamActivity {

	private final SendNotificationService sendNotificationService;

	public GetSendNotificationEventsFromStreamActivityImpl(SendNotificationService sendNotificationService) {
		this.sendNotificationService = sendNotificationService;
	}

	@Override
	public List<ProgressResponseElementV28DTO> fetchSendNotificationEventsFromStream(Long organizationId, String sendStreamId) {
		log.info("Retrieve SEND event from stream {} for organization {}", sendStreamId, organizationId);
		return sendNotificationService.readSendStreamEvents(organizationId, sendStreamId);
	}
}
