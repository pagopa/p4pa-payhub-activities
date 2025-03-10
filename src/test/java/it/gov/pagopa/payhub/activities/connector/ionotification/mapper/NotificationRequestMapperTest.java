package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationRequestMapperTest {

    private NotificationRequestMapper mapper;

    @BeforeEach
    void init() {
        mapper = new NotificationRequestMapper();
    }

    @Test
    void givenMapDebtPositionDTO2NotificationRequestDTOThenSuccess() {
        // Given
        DebtPositionDTO debtPosition = DebtPositionFaker.buildDebtPositionDTO();
        String serviceId = "serviceId";
        String subject = "subject";
        String markdown = "markdown";
        NotificationRequestDTO expectedResult = NotificationRequestDTO.builder()
                .fiscalCode(debtPosition.getPaymentOptions().getFirst().getInstallments().getFirst().getDebtor().getFiscalCode())
                .orgId(debtPosition.getOrganizationId())
                .debtPositionTypeOrgId(debtPosition.getDebtPositionTypeOrgId())
                .serviceId(serviceId)
                .subject(subject)
                .markdown(markdown)
                .build();

        // When
        List<NotificationRequestDTO> result =
                mapper.mapDebtPositionDTO2NotificationRequestDTO(debtPosition, serviceId, subject, markdown);

        // Then
        // TODO fix test
        checkNotNullFields(result.getFirst(), "operationType", "iuv", "paymentReason", "paymentDate", "amount", "apiKey", "dueDate", "nav");
        assertEquals(expectedResult, result.getFirst());
        assertEquals("uniqueIdentifierCode", result.getFirst().getFiscalCode());
    }
}
