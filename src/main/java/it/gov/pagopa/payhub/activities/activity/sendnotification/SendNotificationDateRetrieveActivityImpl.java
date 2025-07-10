package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.debtposition.dto.generated.UpdateInstallmentNotificationDateRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationPaymentsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Lazy
public class SendNotificationDateRetrieveActivityImpl implements SendNotificationDateRetrieveActivity {
    private final SendService sendService;
    private final DebtPositionService debtPositionService;

    public SendNotificationDateRetrieveActivityImpl(SendService sendService, DebtPositionService debtPositionService) {
        this.sendService = sendService;
        this.debtPositionService = debtPositionService;
    }

    @Override
    public SendNotificationDTO sendNotificationDateRetrieve(String sendNotificationId) {
        SendNotificationDTO sendNotificationDTO = sendService.retrieveNotificationDate(sendNotificationId);
        sendNotificationDTO.getPayments().stream()
                .filter(payment -> payment.getNotificationDate() != null)
                .forEach(sendNotificationPayments -> {
                    UpdateInstallmentNotificationDateRequest request = UpdateInstallmentNotificationDateRequest.builder()
                            .debtPositionId(sendNotificationPayments.getDebtPositionId())
                            .nav(sendNotificationPayments.getNavList())
                            .notificationDate(sendNotificationPayments.getNotificationDate())
                            .build();

                    debtPositionService.updateInstallmentNotificationDate(request);
                });

        List<SendNotificationPaymentsDTO> paymentsWithoutDate = sendNotificationDTO.getPayments().stream()
                .filter(payment -> payment.getNotificationDate() == null)
                .toList();

        if (paymentsWithoutDate.isEmpty()) {
            return sendNotificationDTO;
        }

        return null;
    }
}
