package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.mapper.NotificationQueueMapper;
import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.SendDebtPositionIONotificationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class SendDebtPositionIONotificationActivityImpl implements SendDebtPositionIONotificationActivity {

    private final SendDebtPositionIONotificationService sendDebtPositionIONotificationService;
    private final NotificationQueueMapper notificationQueueMapper;

    public SendDebtPositionIONotificationActivityImpl(SendDebtPositionIONotificationService sendDebtPositionIONotificationService, NotificationQueueMapper notificationQueueMapper) {
        this.sendDebtPositionIONotificationService = sendDebtPositionIONotificationService;
        this.notificationQueueMapper = notificationQueueMapper;
    }

    @Override
    public void sendMessage(DebtPositionDTO debtPosition) {
        sendDebtPositionIONotificationService
                .sendMessage(notificationQueueMapper.mapDebtPositionDTO2NotificationQueueDTO(debtPosition));
    }
}
