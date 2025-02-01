package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationQueueDTO;

public class NotificationQueueFaker {

    public static NotificationQueueDTO buildNotificationQueueDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(NotificationQueueDTO.class)
                .fiscalCode("uniqueIdentifierCode")
                .enteId(2L)
                .tipoDovutoId(3L);
    }
}
