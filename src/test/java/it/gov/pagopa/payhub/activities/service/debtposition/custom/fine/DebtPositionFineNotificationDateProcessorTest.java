package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.dto.debtposition.HandleFineDebtPositionResult;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.FineWfExecutionConfig;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO2;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionFineNotificationDateProcessorTest {

    private DebtPositionFineNotificationDateProcessor processor;

    @BeforeEach
    void setUp(){
        processor = new DebtPositionFineNotificationDateProcessor();
    }

    @Test
    void givenProcessNotificationDateWhenDueDateEqualToEndDatesThenReturnFalse() {
        // Given
        // Create a PaymentOption of type REDUCED_SINGLE_INSTALLMENT
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);

        // Create the related Installment with a notification date (2 days ago)
        InstallmentDTO installmentDTO1 = buildInstallmentDTO();
        installmentDTO1.setNotificationDate(OffsetDateTime.now().minusDays(2));

        // Set the current dueDate to match expected reduction logic (notificationDate + 5 days)
        installmentDTO1.setDueDate(Objects.requireNonNull(installmentDTO1.getNotificationDate()).plusDays(5).atZoneSameInstant(Utilities.ZONEID).toLocalDate());
        paymentOptionDTO1.setInstallments(List.of(installmentDTO1));

        // Create a PaymentOption of type SINGLE_INSTALLMENT
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        InstallmentDTO installmentDTO2 = buildInstallmentDTO2();
        installmentDTO2.setNotificationDate(OffsetDateTime.now().minusDays(2));

        // Set dueDate to match expiration logic (notificationDate + 60 days)
        installmentDTO2.setDueDate(Objects.requireNonNull(installmentDTO2.getNotificationDate()).plusDays(60).atZoneSameInstant(Utilities.ZONEID).toLocalDate());

        paymentOptionDTO2.setInstallments(List.of(installmentDTO2));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));

        FineWfExecutionConfig fineWfExecutionConfig = new FineWfExecutionConfig();

        // When
        HandleFineDebtPositionResult result = processor.processNotificationDate(debtPositionDTO, fineWfExecutionConfig);

        // Then
        assertEquals(installmentDTO1.getDueDate(), result.getReductionEndDate().atZoneSameInstant(Utilities.ZONEID).toLocalDate());
        assertFalse(result.isNotified());
    }

    @Test
    void givenProcessNotificationDateWhenDueDateOfReducedPONotEqualThenReturnTrue() {
        // Given
        // Create a PaymentOption of type REDUCED_SINGLE_INSTALLMENT
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);

        // Create the related Installment with a notification date (2 days ago)
        InstallmentDTO installmentDTO1 = buildInstallmentDTO();
        installmentDTO1.setNotificationDate(OffsetDateTime.now().minusDays(2));

        // Set the current dueDate to match expected reduction logic (notificationDate + 5 days)
        installmentDTO1.setDueDate(Objects.requireNonNull(installmentDTO1.getNotificationDate()).plusDays(5).atZoneSameInstant(Utilities.ZONEID).toLocalDate());
        paymentOptionDTO1.setInstallments(List.of(installmentDTO1));

        // Create a PaymentOption of type SINGLE_INSTALLMENT
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        InstallmentDTO installmentDTO2 = buildInstallmentDTO2();
        installmentDTO2.setNotificationDate(OffsetDateTime.now().minusDays(2));

        // Due date is different (40 days ago → should trigger update)
        installmentDTO2.setDueDate(Objects.requireNonNull(installmentDTO2.getNotificationDate()).plusDays(40).atZoneSameInstant(Utilities.ZONEID).toLocalDate());

        paymentOptionDTO2.setInstallments(List.of(installmentDTO2));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));

        FineWfExecutionConfig fineWfExecutionConfig = new FineWfExecutionConfig();

        // When
        HandleFineDebtPositionResult result = processor.processNotificationDate(debtPositionDTO, fineWfExecutionConfig);

        // Then
        assertEquals(installmentDTO1.getDueDate(), result.getReductionEndDate().atZoneSameInstant(Utilities.ZONEID).toLocalDate());
        assertTrue(result.isNotified());
    }

    @Test
    void givenProcessNotificationDateWhenDueDateOfSinglePONotEqualThenReturnTrue() {
        // Given
        // Create a PaymentOption of type REDUCED_SINGLE_INSTALLMENT
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);

        // Create the related Installment with a notification date (2 days ago)
        InstallmentDTO installmentDTO1 = buildInstallmentDTO();
        installmentDTO1.setNotificationDate(OffsetDateTime.now().minusDays(2));

        // Due date is different (2 days ago → should trigger update)
        installmentDTO1.setDueDate(Objects.requireNonNull(installmentDTO1.getNotificationDate()).plusDays(2).atZoneSameInstant(Utilities.ZONEID).toLocalDate());
        paymentOptionDTO1.setInstallments(List.of(installmentDTO1));

        // Create a PaymentOption of type SINGLE_INSTALLMENT
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        InstallmentDTO installmentDTO2 = buildInstallmentDTO2();
        installmentDTO2.setNotificationDate(OffsetDateTime.now().minusDays(2));

        // Set dueDate to match expiration logic (notificationDate + 60 days)
        installmentDTO2.setDueDate(Objects.requireNonNull(installmentDTO2.getNotificationDate()).plusDays(60).atZoneSameInstant(Utilities.ZONEID).toLocalDate());

        paymentOptionDTO2.setInstallments(List.of(installmentDTO2));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));


        FineWfExecutionConfig fineWfExecutionConfig = new FineWfExecutionConfig();

        // When
        HandleFineDebtPositionResult result = processor.processNotificationDate(debtPositionDTO, fineWfExecutionConfig);

        // Then
        assertEquals(installmentDTO1.getDueDate(), result.getReductionEndDate().atZoneSameInstant(Utilities.ZONEID).toLocalDate());
        assertTrue(result.isNotified());
    }

    @Test
    void givenProcessNotificationDateWhenNotificationIdNullThenReturnFalse() {
        // Given
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);
        InstallmentDTO installmentDTO1 = buildInstallmentDTO();
        installmentDTO1.setNotificationDate(null);
        paymentOptionDTO1.setInstallments(List.of(installmentDTO1));

        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        InstallmentDTO installmentDTO2 = buildInstallmentDTO2();
        installmentDTO2.setNotificationDate(null);
        paymentOptionDTO2.setInstallments(List.of(installmentDTO2));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1, paymentOptionDTO2));


        FineWfExecutionConfig fineWfExecutionConfig = new FineWfExecutionConfig();

        // When
        HandleFineDebtPositionResult result = processor.processNotificationDate(debtPositionDTO, fineWfExecutionConfig);

        // Then
        assertFalse(result.isNotified());
    }

    @Test
    void givenPOWithUnknownTypeThenSkipProcessing() {
        // Given
        PaymentOptionDTO po = new PaymentOptionDTO();
        po.setPaymentOptionType(PaymentOptionTypeEnum.DOWN_PAYMENT);

        InstallmentDTO installment = buildInstallmentDTO();
        installment.setNotificationDate(OffsetDateTime.now().minusDays(2));

        po.setInstallments(List.of(installment));

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(po));

        FineWfExecutionConfig config = new FineWfExecutionConfig();

        // When
        HandleFineDebtPositionResult result = processor.processNotificationDate(debtPositionDTO, config);

        // Then
        assertFalse(result.isNotified());
    }
}
