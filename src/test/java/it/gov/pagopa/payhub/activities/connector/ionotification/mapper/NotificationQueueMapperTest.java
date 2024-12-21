package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.payhub.activities.dto.PersonDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.PaymentOptionDTO;
import it.gov.pagopa.pu.p4paionotification.dto.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.utility.faker.DebtPositionTypeOrgFaker.buildDebtPositionTypeOrgDTO;
import static it.gov.pagopa.payhub.activities.utility.faker.NotificationQueueFaker.buildNotificationQueueDTO;
import static it.gov.pagopa.payhub.activities.utility.faker.OrganizationFaker.buildOrganizationDTO;
import static it.gov.pagopa.payhub.activities.utility.faker.PaymentOptionFaker.buildPaymentOptionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationQueueMapperTest {

    private NotificationQueueMapper mapper;

    @BeforeEach
    void init() {
        mapper = new NotificationQueueMapper();
    }

    @Test
    void givenMapDebtPositionDTO2NotificationQueueDTOThenSuccess() {

        DebtPositionDTO debtPosition = buildDebtPosition();

        List<NotificationQueueDTO> result =
                mapper.mapDebtPositionDTO2NotificationQueueDTO(debtPosition);

        checkNotNullFields(result.get(0), "operationType", "iuv", "paymentReason", "paymentDate", "amount");
        assertEquals(buildNotificationQueueDTO(), result.get(0));
        assertEquals("fiscalCode", result.get(1).getFiscalCode());
    }

    private static DebtPositionDTO buildDebtPosition() {
        return DebtPositionDTO.builder()
                .org(buildOrganizationDTO())
                .debtPositionTypeOrg(buildDebtPositionTypeOrgDTO())
                .paymentOptions(List.of(
                        buildPaymentOptionDTO(),
                        PaymentOptionDTO.builder()
                                .installments(List.of(
                                        InstallmentDTO.builder()
                                                .payer(PersonDTO.builder()
                                                        .uniqueIdentifierCode("fiscalCode")
                                                        .build())
                                                .build()))
                                .build()))
                .build();
    }
}
