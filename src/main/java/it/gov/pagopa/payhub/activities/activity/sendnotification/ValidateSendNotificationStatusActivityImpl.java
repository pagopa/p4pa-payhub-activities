package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendStreamSkippedEventException;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
@Lazy
public class ValidateSendNotificationStatusActivityImpl implements ValidateSendNotificationStatusActivity {

	private final SendService sendService;
	private final InstallmentService installmentService;

	public ValidateSendNotificationStatusActivityImpl(SendService sendService, InstallmentService installmentService) {
		this.sendService = sendService;
		this.installmentService = installmentService;
	}

	@Override
	public SendNotificationDTO validateSendNotificationStatus(String notificationRequestId) {
		log.info("Starting sendNotificationStatusValidity for notificationRequestId {}", notificationRequestId);
		SendNotificationDTO sendNotificationDTOByRequestId;
		try {
			sendNotificationDTOByRequestId = sendService.retrieveNotificationByNotificationRequestId(notificationRequestId);
		} catch (HttpClientErrorException.NotFound e) {
			String errorMessage = "Notification for notificationRequestId %s not found: error message %s".formatted(notificationRequestId, e.getMessage());
			throw new SendStreamSkippedEventException("Skipped an error during execution of activity %s: %s".formatted(ValidateSendNotificationStatusActivity.class.getSimpleName(), errorMessage));
		}
		if(sendNotificationDTOByRequestId == null) {
			return null;
		}
		SendNotificationDTO sendNotificationDTO = sendService.notificationStatus(sendNotificationDTOByRequestId.getSendNotificationId());
		if(sendNotificationDTO!=null && sendNotificationDTO.getIun()!=null) {
			sendNotificationDTO.getPayments()
					.stream()
					.filter(p -> p.getDebtPositionId() != null)
					.forEach(p ->
							installmentService.updateIunByDebtPositionId(
									p.getDebtPositionId(),
									sendNotificationDTO.getIun()
							)
					);
		}
		return sendNotificationDTO;
	}
}
