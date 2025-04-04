package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO2;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IoNotificationFinePlaceholderResolverServiceTest {

    private IoNotificationFinePlaceholderResolverService service;

    @BeforeEach
    void setUp() {
        service = new IoNotificationFinePlaceholderResolverService();
    }

    @Test
    void whenApplyFinePlaceholderThenOk() {
        // Given
        LocalDate notificationDateRidotto = LocalDate.of(2024, 4, 1);
        LocalDate dueDateRidotto = notificationDateRidotto.plusDays(5);
        LocalDate dueDateIntero = notificationDateRidotto.plusDays(60);

        OffsetDateTime notificationOffset = notificationDateRidotto.atStartOfDay().atOffset(ZoneOffset.UTC);

        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);
        InstallmentDTO installmentDTO1 = buildInstallmentDTO();
        installmentDTO1.setIuv("iuvRidotto");
        installmentDTO1.setNav("navRidotto");
        installmentDTO1.setAmountCents(100L);
        installmentDTO1.setNotificationDate(notificationOffset);
        installmentDTO1.setDueDate(dueDateRidotto);
        paymentOptionDTO1.setInstallments(List.of(installmentDTO1));

        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        InstallmentDTO installmentDTO2 = buildInstallmentDTO2();
        installmentDTO2.setIuv("iuvIntero");
        installmentDTO2.setNav("navIntero");
        installmentDTO2.setAmountCents(200L);
        installmentDTO2.setNotificationDate(notificationOffset);
        installmentDTO2.setDueDate(dueDateIntero);
        paymentOptionDTO2.setInstallments(List.of(installmentDTO2));

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));

        String markdown = " Avviso ridotto: IUV=%avvisoRidotto_IUV%, NAV=%avvisoRidotto_NAV%, importo=%avvisoRidotto_importo%, scadenza=%fineRiduzione%, notifica=%dataNotifica%. "+
            "Avviso intero: IUV=%avvisoIntero_IUV%, NAV=%avvisoIntero_NAV%, importo=%avvisoIntero_importo%, scadenza=%posizioneDebitoria_scadenza%.";

        String expectedMessage = "Avviso ridotto: IUV=iuvRidotto, NAV=navRidotto, importo=1,00, scadenza=06/04/2024, notifica=01/04/2024. " +
            "Avviso intero: IUV=iuvIntero, NAV=navIntero, importo=2,00, scadenza=31/05/2024.";

        // When
        String message = service.applyFinePlaceholder(markdown, debtPositionDTO);

        // Then
        assertEquals(expectedMessage, message);
    }

    @Test
    void givenReducedInstallmentNullWhenThenOnlySingleDataIsFilled() {
        // Given
        InstallmentDTO single = buildInstallmentDTO();
        single.setIuv("iuvIntero");
        single.setNav("navIntero");
        single.setAmountCents(200L);
        single.setNotificationDate(OffsetDateTime.parse("2024-04-01T10:00:00Z"));
        single.setDueDate(LocalDate.of(2024, 5, 31));

        PaymentOptionDTO poSingle = new PaymentOptionDTO();
        poSingle.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        poSingle.setInstallments(List.of(single));

        DebtPositionDTO dto = new DebtPositionDTO();
        dto.setPaymentOptions(List.of(poSingle));

        String markdown = "2Ridotto: %avvisoRidotto_IUV%, %avvisoRidotto_importo%, %fineRiduzione%, %dataNotifica%. " +
            "Intero: %avvisoIntero_IUV%, %avvisoIntero_importo%, %posizioneDebitoria_scadenza%.";

        String expected = "Ridotto: , , , . " +
            "Intero: iuvIntero, 2,00, 31/05/2024.";

        // When
        String result = service.applyFinePlaceholder(markdown, dto);

        // Then
        assertEquals(expected, result);
    }

    @Test
    void givenSingleInstallmentNullWhenThenOnlyReducedDataIsFilled() {
        // Given
        InstallmentDTO reduced = buildInstallmentDTO();
        reduced.setIuv("iuvRidotto");
        reduced.setNav("navRidotto");
        reduced.setAmountCents(100L);
        reduced.setNotificationDate(OffsetDateTime.parse("2024-04-01T10:00:00Z"));
        reduced.setDueDate(LocalDate.of(2024, 4, 6));

        PaymentOptionDTO poReduced = new PaymentOptionDTO();
        poReduced.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);
        poReduced.setInstallments(List.of(reduced));

        DebtPositionDTO dto = new DebtPositionDTO();
        dto.setPaymentOptions(List.of(poReduced));

        String markdown = "Ridotto: %avvisoRidotto_IUV%, %avvisoRidotto_importo%, %fineRiduzione%, %dataNotifica%. " +
            "Intero: %avvisoIntero_IUV%, %avvisoIntero_importo%, %posizioneDebitoria_scadenza%.";

        String expected = "Ridotto: iuvRidotto, 1,00, 06/04/2024, 01/04/2024. " +
            "Intero: , , .";

        // When
        String result = service.applyFinePlaceholder(markdown, dto);

        // Then
        assertEquals(expected, result);
    }
}
