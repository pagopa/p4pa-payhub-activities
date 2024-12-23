package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationService;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class SendDebtPositionIONotificationActivityImpl implements SendDebtPositionIONotificationActivity {

    private final IONotificationService ioNotificationService;

    public SendDebtPositionIONotificationActivityImpl(IONotificationService ioNotificationService) {
        this.ioNotificationService = ioNotificationService;
    }

    @Override
    public void sendMessage(DebtPositionDTO debtPosition) {
        log.info("Sending message to IONotification for debt position type org {}", debtPosition.getDebtPositionTypeOrg());
        ioNotificationService.sendMessage(debtPosition);
    }
}
