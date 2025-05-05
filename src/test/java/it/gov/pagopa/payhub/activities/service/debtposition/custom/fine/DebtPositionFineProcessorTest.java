package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.PaymentOptionService;
import it.gov.pagopa.payhub.activities.dto.debtposition.HandleFineDebtPositionResult;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DebtPositionFineProcessorTest {

    @Mock
    private InstallmentService installmentServiceMock;
    @Mock
    private PaymentOptionService paymentOptionServiceMock;

    private DebtPositionFineProcessor processor;

    @BeforeEach
    void setUp(){
        processor = new DebtPositionFineProcessor(installmentServiceMock, paymentOptionServiceMock);
    }

    @Test
    void whenProcessFineThenOk(){
        // Given
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        paymentOptionDTO1.setStatus(PaymentOptionStatus.TO_SYNC);

        InstallmentDTO installmentDTO1 = buildInstallmentDTO();
        installmentDTO1.setStatus(InstallmentStatus.TO_SYNC);
        installmentDTO1.setSyncStatus(new InstallmentSyncStatus(InstallmentStatus.DRAFT, InstallmentStatus.UNPAID, null));
        paymentOptionDTO1.setInstallments(List.of(installmentDTO1));

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1));

        HandleFineDebtPositionResult handleFineDebtPositionResult = new HandleFineDebtPositionResult(debtPositionDTO, OffsetDateTime.now().plusDays(2), true);

        // When
        processor.processFine(handleFineDebtPositionResult);

        //Then
        assertEquals(PaymentOptionStatus.UNPAYABLE, handleFineDebtPositionResult.getDebtPositionDTO().getPaymentOptions().getFirst().getStatus());
        assertEquals(InstallmentStatus.UNPAYABLE, handleFineDebtPositionResult.getDebtPositionDTO().getPaymentOptions().getFirst().getInstallments().getFirst().getStatus());
        verify(installmentServiceMock).updateStatusAndSyncStatus(installmentDTO1.getInstallmentId(), InstallmentStatus.UNPAYABLE, null);
        verify(paymentOptionServiceMock).updateStatus(paymentOptionDTO1.getPaymentOptionId(), PaymentOptionStatus.UNPAYABLE);

    }

    @Test
    void givenPOIsNotToSyncWhenProcessFineThenOk(){
        // Given
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.REDUCED_SINGLE_INSTALLMENT);
        paymentOptionDTO1.setStatus(PaymentOptionStatus.PAID);

        InstallmentDTO installmentDTO1 = buildInstallmentDTO();
        installmentDTO1.setStatus(InstallmentStatus.TO_SYNC);
        installmentDTO1.setSyncStatus(new InstallmentSyncStatus(InstallmentStatus.DRAFT, InstallmentStatus.UNPAID, null));
        paymentOptionDTO1.setInstallments(List.of(installmentDTO1));

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1));

        HandleFineDebtPositionResult handleFineDebtPositionResult = new HandleFineDebtPositionResult(debtPositionDTO, OffsetDateTime.now().plusDays(2), true);

        // When
        processor.processFine(handleFineDebtPositionResult);

        //Then
        assertEquals(PaymentOptionStatus.PAID, handleFineDebtPositionResult.getDebtPositionDTO().getPaymentOptions().getFirst().getStatus());
        assertEquals(InstallmentStatus.TO_SYNC, handleFineDebtPositionResult.getDebtPositionDTO().getPaymentOptions().getFirst().getInstallments().getFirst().getStatus());
    }

    @Test
    void givenInstallmentIsNotToSyncWhenProcessFineThenOk(){
        // Given
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        paymentOptionDTO1.setStatus(PaymentOptionStatus.TO_SYNC);

        InstallmentDTO installmentDTO1 = buildInstallmentDTO();
        installmentDTO1.setStatus(InstallmentStatus.PAID);
        installmentDTO1.setSyncStatus(new InstallmentSyncStatus(InstallmentStatus.DRAFT, InstallmentStatus.PAID, null));
        paymentOptionDTO1.setInstallments(List.of(installmentDTO1));

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1));

        HandleFineDebtPositionResult handleFineDebtPositionResult = new HandleFineDebtPositionResult(debtPositionDTO, OffsetDateTime.now().plusDays(2), true);

        // When
        processor.processFine(handleFineDebtPositionResult);

        //Then
        assertEquals(PaymentOptionStatus.TO_SYNC, handleFineDebtPositionResult.getDebtPositionDTO().getPaymentOptions().getFirst().getStatus());
        assertEquals(InstallmentStatus.PAID, handleFineDebtPositionResult.getDebtPositionDTO().getPaymentOptions().getFirst().getInstallments().getFirst().getStatus());
    }

    @Test
    void givenReductionDateNotBeforeWhenProcessFineThenNowThenOk(){
        // Given
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        paymentOptionDTO1.setStatus(PaymentOptionStatus.TO_SYNC);

        InstallmentDTO installmentDTO1 = buildInstallmentDTO();
        installmentDTO1.setStatus(InstallmentStatus.TO_SYNC);
        installmentDTO1.setSyncStatus(new InstallmentSyncStatus(InstallmentStatus.DRAFT, InstallmentStatus.UNPAID, null));
        paymentOptionDTO1.setInstallments(List.of(installmentDTO1));

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1));

        HandleFineDebtPositionResult handleFineDebtPositionResult = new HandleFineDebtPositionResult(debtPositionDTO, OffsetDateTime.now().minusDays(1), true);

        // When
        processor.processFine(handleFineDebtPositionResult);

        //Then
        assertEquals(PaymentOptionStatus.TO_SYNC, handleFineDebtPositionResult.getDebtPositionDTO().getPaymentOptions().getFirst().getStatus());
        assertEquals(InstallmentStatus.TO_SYNC, handleFineDebtPositionResult.getDebtPositionDTO().getPaymentOptions().getFirst().getInstallments().getFirst().getStatus());
    }

    @Test
    void givenReductionDateNullWhenProcessFineThenOk(){
        // Given
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        paymentOptionDTO1.setStatus(PaymentOptionStatus.TO_SYNC);

        InstallmentDTO installmentDTO1 = buildInstallmentDTO();
        installmentDTO1.setStatus(InstallmentStatus.TO_SYNC);
        installmentDTO1.setSyncStatus(new InstallmentSyncStatus(InstallmentStatus.DRAFT, InstallmentStatus.UNPAID, null));
        paymentOptionDTO1.setInstallments(List.of(installmentDTO1));

        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO1));

        HandleFineDebtPositionResult handleFineDebtPositionResult = new HandleFineDebtPositionResult(debtPositionDTO, null, true);

        // When
        processor.processFine(handleFineDebtPositionResult);

        //Then
        verify(installmentServiceMock).updateStatusAndSyncStatus(installmentDTO1.getInstallmentId(), InstallmentStatus.UNPAYABLE, null);
        verify(paymentOptionServiceMock).updateStatus(paymentOptionDTO1.getPaymentOptionId(), PaymentOptionStatus.UNPAYABLE);
        assertEquals(PaymentOptionStatus.UNPAYABLE, handleFineDebtPositionResult.getDebtPositionDTO().getPaymentOptions().getFirst().getStatus());
        assertEquals(InstallmentStatus.UNPAYABLE, handleFineDebtPositionResult.getDebtPositionDTO().getPaymentOptions().getFirst().getInstallments().getFirst().getStatus());
    }
}
