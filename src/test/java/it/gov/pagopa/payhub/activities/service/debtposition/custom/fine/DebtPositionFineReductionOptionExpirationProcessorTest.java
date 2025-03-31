package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.PaymentOptionService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionFineReductionOptionExpirationProcessorTest {

    @Mock
    private DebtPositionService debtPositionServiceMock;
    @Mock
    private PaymentOptionService paymentOptionServiceMock;
    @Mock
    private InstallmentService installmentServiceMock;

    private DebtPositionFineReductionOptionExpirationProcessor processor;

    @BeforeEach
    void setUp(){
        processor = new DebtPositionFineReductionOptionExpirationProcessor(
                debtPositionServiceMock,
                paymentOptionServiceMock,
                installmentServiceMock);
    }

    @Test
    void givenDPNotPaidOrReportedWhenHandleFineReductionExpirationThenReturnUpdatedDP(){
        // Given
        Long debtPositionId = 1L;
        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setStatus(DebtPositionStatus.UNPAID);
        PaymentOptionDTO paymentOptionDTO = new PaymentOptionDTO();
        paymentOptionDTO.setPaymentOptionId(1L);
        InstallmentDTO installmentDTO = buildInstallmentDTO2();
        installmentDTO.setInstallmentId(1L);
        paymentOptionDTO.setInstallments(List.of(installmentDTO));
        paymentOptionDTO.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        paymentOptionDTO.setStatus(PaymentOptionStatus.UNPAYABLE);
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO));
        InstallmentSyncStatus syncStatus = new InstallmentSyncStatus();
        syncStatus.setSyncStatusFrom(InstallmentStatus.UNPAYABLE);
        syncStatus.setSyncStatusTo(InstallmentStatus.UNPAID);

        when(debtPositionServiceMock.getDebtPosition(debtPositionId))
                .thenReturn(debtPositionDTO);

        // When
        DebtPositionDTO result = processor.handleFineReductionExpiration(debtPositionId);

        // The
        assertEquals(PaymentOptionStatus.TO_SYNC, result.getPaymentOptions().getFirst().getStatus());
        assertEquals(InstallmentStatus.TO_SYNC, result.getPaymentOptions().getFirst().getInstallments().getFirst().getStatus());
        verify(paymentOptionServiceMock).updateStatus(paymentOptionDTO.getPaymentOptionId(), PaymentOptionStatus.TO_SYNC);
        verify(installmentServiceMock).updateStatusAndSyncStatus(installmentDTO.getInstallmentId(), InstallmentStatus.TO_SYNC, syncStatus);
    }

    @Test
    void givenDPNullWhenHandleFineReductionExpirationThenReturnNull(){
        // Given
        Long debtPositionId = 1L;

        when(debtPositionServiceMock.getDebtPosition(debtPositionId))
                .thenReturn(null);

        // When
        DebtPositionDTO result = processor.handleFineReductionExpiration(debtPositionId);

        // Then
        assertNull(result);
    }

    @Test
    void givenDPPaidWhenHandleFineReductionExpirationThenReturnNull(){
        // Given
        Long debtPositionId = 1L;
        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setStatus(DebtPositionStatus.PAID);

        when(debtPositionServiceMock.getDebtPosition(debtPositionId))
                .thenReturn(debtPositionDTO);

        // When
        DebtPositionDTO result = processor.handleFineReductionExpiration(debtPositionId);

        // Then
        assertNull(result);
    }

    @Test
    void givenDPReportedWhenHandleFineReductionExpirationThenReturnNull(){
        // Given
        Long debtPositionId = 1L;
        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        debtPositionDTO.setStatus(DebtPositionStatus.REPORTED);

        when(debtPositionServiceMock.getDebtPosition(debtPositionId))
                .thenReturn(debtPositionDTO);

        // When
        DebtPositionDTO result = processor.handleFineReductionExpiration(debtPositionId);

        // Then
        assertNull(result);
    }
}
