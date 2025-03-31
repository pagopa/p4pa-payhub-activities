package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.debtposition.dto.generated.UpdateInstallmentNotificationDateRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@Lazy
public class RetrieveNotificationDateActivityImpl implements RetrieveNotificationDateActivity {
    private final SendService sendService;
    private final DebtPositionService debtPositionService;

    public RetrieveNotificationDateActivityImpl(SendService sendService, DebtPositionService debtPositionService) {
        this.sendService = sendService;
        this.debtPositionService = debtPositionService;
    }

    @Override
    public SendNotificationDTO retrieveNotificationDate(String sendNotificationId) {
        SendNotificationDTO sendNotificationDTO = sendService.retrieveNotificationDate(sendNotificationId);
        if (sendNotificationDTO != null && sendNotificationDTO.getNotificationDate() != null) {
            OffsetDateTime notificationDate = sendNotificationDTO.getNotificationDate();

            sendNotificationDTO.getPayments()
                    .forEach(sendNotificationPayments -> {
                        UpdateInstallmentNotificationDateRequest request = UpdateInstallmentNotificationDateRequest.builder()
                                .debtPositionId(sendNotificationPayments.getDebtPositionId())
                                .nav(sendNotificationPayments.getNavList())
                                .notificationDate(notificationDate)
                                .build();

                        debtPositionService.updateInstallmentNotificationDate(request);
                    });

            return sendNotificationDTO;
        }
        return null;
    }
}
