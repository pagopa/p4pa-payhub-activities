package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
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
        // Given
        DebtPositionDTO debtPosition = DebtPositionFaker.buildDebtPositionDTO();
        NotificationQueueDTO expectedResult = NotificationQueueDTO.builder()
                .fiscalCode(debtPosition.getPaymentOptions().getFirst().getInstallments().getFirst().getDebtor().getFiscalCode())
                .enteId(debtPosition.getOrganizationId())
                .tipoDovutoId(debtPosition.getDebtPositionTypeOrgId())
                .build();

        // When
        List<NotificationQueueDTO> result =
                mapper.mapDebtPositionDTO2NotificationQueueDTO(debtPosition);

        // Then
        checkNotNullFields(result.getFirst(), "operationType", "iuv", "paymentReason", "paymentDate", "amount");
        assertEquals(expectedResult, result.getFirst());
        assertEquals("uniqueIdentifierCode", result.getFirst().getFiscalCode());
    }
}
