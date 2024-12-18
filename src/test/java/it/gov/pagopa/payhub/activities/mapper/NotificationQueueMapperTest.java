package it.gov.pagopa.payhub.activities.mapper;

import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.utility.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.utility.faker.NotificationQueueFaker.buildNotificationQueueDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationQueueMapperTest {

    private NotificationQueueMapper mapper;

    @BeforeEach
    void init(){
        mapper = new NotificationQueueMapper();
    }

    @Test
    void givenMapDebtPositionDTO2NotificationQueueDTOThenSuccess(){

        NotificationQueueDTO result =
                mapper.mapDebtPositionDTO2NotificationQueueDTO(buildDebtPositionDTO());

        checkNotNullFields(result, "operationType");
        assertEquals(buildNotificationQueueDTO(), result);
    }
}
