package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class NotificationStatusActivityImpl implements NotificationStatusActivity {
    private final SendService sendService;
    private final DebtPositionService debtPositionService;
    private final InstallmentService installmentService;

    public NotificationStatusActivityImpl(SendService sendService,
        DebtPositionService debtPositionService,
        InstallmentService installmentService){
        this.sendService = sendService;
      this.debtPositionService = debtPositionService;
      this.installmentService = installmentService;
    }

    @Override
    public SendNotificationDTO getSendNotificationStatus(String sendNotificationId) {
        log.info("Starting notificationStatus for sendNotificationId {}", sendNotificationId);
        SendNotificationDTO sendNotificationDTO = sendService.notificationStatus(sendNotificationId);
        if(sendNotificationDTO!=null && sendNotificationDTO.getIun()!=null) {
            sendNotificationDTO.getPayments().forEach(p -> {
                DebtPositionDTO debtPositionDTO = debtPositionService.getDebtPosition(p.getDebtPositionId());
                debtPositionDTO.getPaymentOptions().forEach(po -> po.getInstallments().stream()
                    .filter(installment -> installment.getIun() != null)
                    .forEach(installment -> installmentService.updateIun(installment.getInstallmentId(), sendNotificationDTO.getIun())));
            });
        }
        return sendNotificationDTO;
    }
}
