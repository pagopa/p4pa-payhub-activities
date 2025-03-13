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
import java.util.Objects;

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

        System.out.println("DueDate: "+Objects.toString(debtPosition.getPaymentOptions().getFirst().getInstallments().getFirst().getDueDate(), ""));

        // When
        List<NotificationRequestDTO> result = mapper.map(
                debtPosition,
                ioNotificationDTO,
                NotificationRequestDTO.OperationTypeEnum.CREATE_DP);

        // Then
        String expectedMarkdown = "Descrizione posizione debitoria: " + debtPosition.getDescription() + ". " +
                "Nome completo debitore: fullName. " +
                "Codice Fiscale debitore: uniqueIdentifierCode. " +
                "Importo totale: 1,00 euro. " +
                "Codice IUV: iuv. " +
                "NAV: nav. " +
                "Causale: remittanceInformation. " +
                "Data di esecuzione pagamento: 15/05/2024.";

        checkNotNullFields(result.getFirst());
        assertEquals(expectedMarkdown, result.getFirst().getMarkdown());
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
        ioNotificationDTO.setIoTemplateMessage("Importo totale: %importoTotale% euro. Data di scadenza %dataScadenza%.");

        // When
        List<NotificationRequestDTO> result = mapper.map(
                debtPosition,
                ioNotificationDTO,
                NotificationRequestDTO.OperationTypeEnum.CREATE_DP);

        // Then
        checkNotNullFields(result.getFirst());
        assertEquals("Importo totale: 1,00 euro. Data di scadenza .", result.getFirst().getMarkdown());

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
                debtPosition,
                ioNotificationDTO,
                NotificationRequestDTO.OperationTypeEnum.CREATE_DP);

        // Then
        checkNotNullFields(result.getFirst(), "nav");
        // size is 1 because fiscalCode is the same for all installments
        assertEquals(1, result.size());
    }
}
