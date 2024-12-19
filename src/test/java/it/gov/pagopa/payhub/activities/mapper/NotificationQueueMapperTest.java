package it.gov.pagopa.payhub.activities.mapper;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.PaymentOptionDTO;
import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.utility.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.utility.faker.NotificationQueueFaker.buildNotificationQueueDTO;
import static it.gov.pagopa.payhub.activities.utility.faker.PaymentOptionFaker.buildPaymentOptionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationQueueMapperTest {

    private NotificationQueueMapper mapper;

    @BeforeEach
    void init(){
        mapper = new NotificationQueueMapper();
    }

    @Test
    void givenMapDebtPositionDTO2NotificationQueueDTOWhenIsMultiDebtorThenSuccess(){

        PaymentOptionDTO paymentOptionDTO = buildPaymentOptionDTO();
        paymentOptionDTO.setMultiDebtor(true);

        DebtPositionDTO debtPosition = buildDebtPositionDTO();
        debtPosition.setPaymentOptions(new ArrayList<>());
        debtPosition.getPaymentOptions().add(paymentOptionDTO);

        List<NotificationQueueDTO> result =
                mapper.mapDebtPositionDTO2NotificationQueueDTO(debtPosition);

        checkNotNullFields(result.get(0), "operationType", "iuv", "paymentReason", "paymentDate", "amount");
        assertEquals(buildNotificationQueueDTO(), result.get(0));
    }

    @Test
    void givenMapDebtPositionDTO2NotificationQueueDTOWhenIsNotMultiDebtorThenSuccess(){

        List<NotificationQueueDTO> result =
                mapper.mapDebtPositionDTO2NotificationQueueDTO(buildDebtPositionDTO());

        checkNotNullFields(result.get(0), "operationType", "iuv", "paymentReason", "paymentDate", "amount");
        assertEquals(buildNotificationQueueDTO(), result.get(0));
    }
}
