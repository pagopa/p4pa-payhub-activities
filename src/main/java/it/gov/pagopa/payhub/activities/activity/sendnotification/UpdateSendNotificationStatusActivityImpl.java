package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class UpdateSendNotificationStatusActivityImpl implements UpdateSendNotificationStatusActivity {

	private final SendService sendService;
	private final InstallmentService installmentService;

	public UpdateSendNotificationStatusActivityImpl(SendService sendService, InstallmentService installmentService) {
		this.sendService = sendService;
		this.installmentService = installmentService;
	}

	@Override
	public SendNotificationDTO updateSendNotificationStatus(String notificationRequestId) {
		log.info("Starting notificationStatus for notificationRequestId {}", notificationRequestId);
		SendNotificationDTO sendNotificationDTOByRequestId = sendService.retrieveNotificationByNotificationRequestId(notificationRequestId);
		if(sendNotificationDTOByRequestId == null) {
			return null;
		}
		SendNotificationDTO sendNotificationDTO = sendService.notificationStatus(sendNotificationDTOByRequestId.getSendNotificationId());
		if(sendNotificationDTO!=null && sendNotificationDTO.getIun()!=null) {
			sendNotificationDTO.getPayments().forEach(p ->
					installmentService.updateIunByDebtPositionId(p.getDebtPositionId(), sendNotificationDTO.getIun()));
		}
		return sendNotificationDTO;
	}
}
