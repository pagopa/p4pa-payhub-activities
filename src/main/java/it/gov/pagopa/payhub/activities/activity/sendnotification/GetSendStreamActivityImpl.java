package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendStreamDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class GetSendStreamActivityImpl implements GetSendStreamActivity {

	private final SendNotificationService sendNotificationService;

	public GetSendStreamActivityImpl(SendNotificationService sendNotificationService) {
		this.sendNotificationService = sendNotificationService;
	}

	@Override
	public SendStreamDTO fetchSendStream(Long organizationId) {
		log.info("Retrieve SEND stream for organization {}", organizationId);
		return sendNotificationService.findSendStream(organizationId);
	}
}
