package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IoNotificationGenericPlaceholderResolverServiceTest {

    private IoNotificationGenericPlaceholderResolverService placeholderResolver;

    @BeforeEach
    void setUp() {
        placeholderResolver = new IoNotificationGenericPlaceholderResolverService();
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
}

