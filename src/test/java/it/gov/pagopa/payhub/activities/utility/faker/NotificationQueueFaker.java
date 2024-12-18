package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;

import java.time.LocalDate;

public class NotificationQueueFaker {

    public static NotificationQueueDTO buildNotificationQueueDTO(){
        return NotificationQueueDTO.builder()
                .fiscalCode("uniqueIdentifierCode")
                .enteId(1L)
                .tipoDovutoId(1L)
                .paymentDate(String.valueOf(LocalDate.of(2099, 5, 15)))
                .amount(String.valueOf(100L))
                .iuv("iuv")
                .paymentReason("remittanceInformation")
                .build();
    }
}
