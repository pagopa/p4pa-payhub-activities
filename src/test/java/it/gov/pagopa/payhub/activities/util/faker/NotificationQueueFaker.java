package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.ionotification.dto.generated.NotificationQueueDTO;

public class NotificationQueueFaker {

    public static NotificationQueueDTO buildNotificationQueueDTO(){
        return NotificationQueueDTO.builder()
                .fiscalCode("uniqueIdentifierCode")
                .enteId(2L)
                .tipoDovutoId(3L)
                .build();
    }
}
