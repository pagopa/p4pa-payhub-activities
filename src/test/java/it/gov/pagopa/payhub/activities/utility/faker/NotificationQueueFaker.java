package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;

public class NotificationQueueFaker {

    public static NotificationQueueDTO buildNotificationQueueDTO(){
        return NotificationQueueDTO.builder()
                .fiscalCode("uniqueIdentifierCode")
                .enteId(1L)
                .tipoDovutoId(1L)
                .build();
    }
}
