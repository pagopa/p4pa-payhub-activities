package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.IONotificationDTOFaker.buildIONotificationDTO;
import static it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO.OperationTypeEnum.CREATE_DP;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationRequestMapperTest {

    private NotificationRequestMapper mapper;

    @BeforeEach
    void init() {
        mapper = new NotificationRequestMapper();
    }

    @Test
    void givenMapWhenOnlyOnePOThenOk() {
        // Given
        DebtPositionDTO debtPosition = DebtPositionFaker.buildDebtPositionDTO();
        IONotificationDTO ioNotificationDTO = buildIONotificationDTO();

        // When
        List<NotificationRequestDTO> result = mapper.map(
                debtPosition.getPaymentOptions(),
                debtPosition.getOrganizationId(),
                debtPosition.getDebtPositionTypeOrgId(),
                "apikey",
                ioNotificationDTO,
                CREATE_DP);

        // Then
        checkNotNullFields(result.getFirst());
    }

    @Test
    void givenMapWhenMoreThenOnePOThenOk() {
        // Given
        DebtPositionDTO debtPosition = DebtPositionFaker.buildDebtPositionDTOWithMultiplePO();
        IONotificationDTO ioNotificationDTO = buildIONotificationDTO();

        // When
        List<NotificationRequestDTO> result = mapper.map(
                debtPosition.getPaymentOptions(),
                debtPosition.getOrganizationId(),
                debtPosition.getDebtPositionTypeOrgId(),
                "apikey",
                ioNotificationDTO,
                CREATE_DP);

        // Then
        checkNotNullFields(result.getFirst(), "nav");
        // size is 1 because fiscalCode is the same for all installments
        assertEquals(1, result.size());
    }
}
