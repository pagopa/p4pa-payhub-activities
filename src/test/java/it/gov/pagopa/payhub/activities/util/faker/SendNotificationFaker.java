package it.gov.pagopa.payhub.activities.util.faker;


import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationStatus;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationPaymentsDTO;

import java.util.Collections;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;

public class SendNotificationFaker {
    public static SendNotificationDTO buildSendNotificationDTO(){
        return SendNotificationDTO.builder()
                .sendNotificationId("sendNotificationId")
                .notificationDate(OFFSETDATETIME)
                .iun("iun")
                .organizationId(1L)
                .payments(Collections.singletonList(SendNotificationPaymentsDTO.builder()
                        .debtPositionId(123L)
                        .navList(Collections.singletonList("nav"))
                        .build()))
                .status(NotificationStatus.ACCEPTED)
                .build();
    }
}
