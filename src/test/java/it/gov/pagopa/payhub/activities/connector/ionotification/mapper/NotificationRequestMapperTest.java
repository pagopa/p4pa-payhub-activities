package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.IONotificationDTOFaker.buildIONotificationDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationRequestMapperTest {

    private NotificationRequestMapper mapper;

    @BeforeEach
    void init() {
        mapper = new NotificationRequestMapper();
    }

    @Test
    void givenMapWhenOnlyOnePOAndMultipleInstallmentThenOk() {
        // Given
        DebtPositionDTO debtPosition = DebtPositionFaker.buildDebtPositionDTO();
        debtPosition.getPaymentOptions().getFirst().setInstallments(List.of(buildInstallmentDTO(),buildInstallmentDTO()));
        IONotificationDTO ioNotificationDTO = buildIONotificationDTO();

        // When
        List<NotificationRequestDTO> result = mapper.map(
                debtPosition.getPaymentOptions(),
                debtPosition.getOrganizationId(),
                debtPosition.getDebtPositionTypeOrgId(),
                ioNotificationDTO);

        // Then
        checkNotNullFields(result.getFirst());
        assertEquals("Causale remittanceInformation. Codice identificativo unico iuv. Totale di 1,00 euro. Data di scadenza 2024-05-15.", result.getFirst().getMarkdown());
        assertEquals(2, result.size());
    }

    @Test
    void givenMapWhenOnlyOnePOWithInstallmentWithoutDueDateThenOk() {
        // Given
        DebtPositionDTO debtPosition = DebtPositionFaker.buildDebtPositionDTO();
        InstallmentDTO installment1 = buildInstallmentDTO();
        installment1.setDueDate(null);
        InstallmentDTO installment2 = buildInstallmentDTO();
        installment2.setDueDate(null);
        debtPosition.getPaymentOptions().getFirst().setInstallments(List.of(installment1,installment2));
        IONotificationDTO ioNotificationDTO = buildIONotificationDTO();
        ioNotificationDTO.setIoTemplateMessage("Totale di %importoDovuto% euro. Data di scadenza %dataEsecuzionePagamento%.");

        // When
        List<NotificationRequestDTO> result = mapper.map(
                debtPosition.getPaymentOptions(),
                debtPosition.getOrganizationId(),
                debtPosition.getDebtPositionTypeOrgId(),
                ioNotificationDTO);

        // Then
        checkNotNullFields(result.getFirst());
        assertEquals("Totale di 1,00 euro. Data di scadenza Not Specified.", result.getFirst().getMarkdown());

        // size is 1 because dueDate is null for all installments
        assertEquals(1, result.size());
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
                ioNotificationDTO);

        // Then
        checkNotNullFields(result.getFirst(), "nav");
        // size is 1 because fiscalCode is the same for all installments
        assertEquals(1, result.size());
    }
}
