package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;

public class IONotificationDTOFaker {

    public static IONotificationDTO buildIONotificationDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(IONotificationDTO.class)
                .serviceId("serviceId")
                .ioTemplateMessage("markdown")
                .ioTemplateSubject("subject");
    }
}
