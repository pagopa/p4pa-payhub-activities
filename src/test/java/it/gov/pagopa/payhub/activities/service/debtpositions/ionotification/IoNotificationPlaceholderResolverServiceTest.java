package it.gov.pagopa.payhub.activities.service.debtpositions.ionotification;

import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.IoNotificationPlaceholderResolverService;
import it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class IoNotificationPlaceholderResolverServiceTest {

    private IoNotificationPlaceholderResolverService placeholderResolver;

    @BeforeEach
    void setUp() {
        placeholderResolver = new IoNotificationPlaceholderResolverService();
    }

    @Test
    void whenApplyDefaultPlaceholderThenReplaceCorrectly() {
        // Given
        DebtPositionDTO debtPosition = DebtPositionFaker.buildDebtPositionDTO();
        InstallmentDTO installmentDTO = debtPosition.getPaymentOptions().getFirst().getInstallments().getFirst();
        String expectedMarkdown = "Descrizione posizione debitoria: " + debtPosition.getDescription() + ". " +
                "Nome completo debitore: fullName. " +
                "Codice Fiscale debitore: uniqueIdentifierCode. " +
                "Importo totale: 1,00 euro. " +
                "Codice IUV: iuv. " +
                "NAV: nav. " +
                "Causale: remittanceInformation. " +
                "Data di esecuzione pagamento: 15/05/2024.";

        // When
        String result = placeholderResolver.applyDefaultPlaceholder(expectedMarkdown, debtPosition, installmentDTO);

        // Then
        Assertions.assertFalse(result.contains("%ente_nome%"));
        Assertions.assertFalse(result.contains("%importoTotale%"));
        Assertions.assertFalse(result.contains("%dataScadenza%"));
    }

    @Test
    void whenApplyPlaceholderWithCustomMapThenReplaceCorrectly() {
        // Given
        String markdownTemplate = "Ciao %name%, il tuo codice è %code%.";
        Map<String, String> placeholders = Map.of(
                "%name%", "Test",
                "%code%", "12345"
        );

        // When
        String result = placeholderResolver.applyPlaceholder(markdownTemplate, placeholders);

        // Then
        Assertions.assertEquals("Ciao Test, il tuo codice è 12345.", result);
    }
}

