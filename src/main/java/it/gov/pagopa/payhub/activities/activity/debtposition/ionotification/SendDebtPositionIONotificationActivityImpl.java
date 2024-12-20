package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.mapper.NotificationQueueMapper;
import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.SendDebtPositionIONotificationService;
import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@Slf4j
public class SendDebtPositionIONotificationActivityImpl implements SendDebtPositionIONotificationActivity {

    private final SendDebtPositionIONotificationService sendDebtPositionIONotificationService;
    private final NotificationQueueMapper notificationQueueMapper;

    public SendDebtPositionIONotificationActivityImpl(SendDebtPositionIONotificationService sendDebtPositionIONotificationService, NotificationQueueMapper notificationQueueMapper) {
        this.sendDebtPositionIONotificationService = sendDebtPositionIONotificationService;
        this.notificationQueueMapper = notificationQueueMapper;
    }

    @Override
    public void sendMessage(DebtPositionDTO debtPosition) {

        log.info("Mapping DebtPositionDTO to NotificationQueueDTO list for orgId {}", debtPosition.getOrg().getOrgId());
        List<NotificationQueueDTO> notificationQueueDTOList = notificationQueueMapper.mapDebtPositionDTO2NotificationQueueDTO(debtPosition);

        for (NotificationQueueDTO notificationQueueDTO : notificationQueueDTOList) {
            log.info("Sending message to IONotification for debt position type org {}", notificationQueueDTO.getTipoDovutoId());
            sendDebtPositionIONotificationService.sendMessage(notificationQueueDTO);
        }
    }
}
