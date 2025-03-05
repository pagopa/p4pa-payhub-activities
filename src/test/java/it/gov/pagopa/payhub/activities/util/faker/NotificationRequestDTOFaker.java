package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;

public class NotificationRequestDTOFaker {

    public static NotificationRequestDTO buildNotificationRequestDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(NotificationRequestDTO.class)
                .fiscalCode("uniqueIdentifierCode")
                .orgId(2L)
                .debtPositionTypeOrgId(3L)
                .serviceId("serviceId")
                .subject("subject")
                .markdown("markdown");
    }
}
