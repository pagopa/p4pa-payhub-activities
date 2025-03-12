package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;

public class NotificationRequestDTOFaker {

    public static NotificationRequestDTO buildNotificationRequestDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(NotificationRequestDTO.class)
                .fiscalCode("fiscalCode")
                .orgId(2L)
                .debtPositionTypeOrgId(3L)
                .serviceId("serviceId")
                .subject("subject")
                .nav("nav")
                .amount(100L)
                .operationType(NotificationRequestDTO.OperationTypeEnum.CREATE_DP)
                .markdown("markdown");
    }
}
