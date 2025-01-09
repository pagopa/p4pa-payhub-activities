package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.ionotification.dto.generated.NotificationQueueDTO;

public class NotificationQueueFaker {

    public static NotificationQueueDTO buildNotificationQueueDTO(){
        return NotificationQueueDTO.builder()
                .fiscalCode("uniqueIdentifierCode")
                .enteId(1L)
                .tipoDovutoId(1L)
                .build();
    }
}
