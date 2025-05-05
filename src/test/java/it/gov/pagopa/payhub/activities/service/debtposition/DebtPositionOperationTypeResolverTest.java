package it.gov.pagopa.payhub.activities.service.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.SyncCompleteDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtPositionOperationTypeResolverTest {

    @Mock
    private InstallmentOperationTypeResolver installmentOperationTypeResolverMock;

    private DebtPositionOperationTypeResolver resolver;

    @BeforeEach
    void setUp(){
        resolver = new DebtPositionOperationTypeResolver(installmentOperationTypeResolverMock);
    }


    @Test
    void givenMapEmptyWhenCalculateDebtPositionOperationTypeThenOk(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setStatus(InstallmentStatus.UNPAID);
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of(installmentDTO, buildInstallmentDTO()));

        // When
        PaymentEventType paymentEventType = resolver.calculateDebtPositionOperationType(debtPositionDTO, Map.of());

        // Then
        assertNull(paymentEventType);
    }

    @Test
    void givenStatusNotEvenOne_TOSYNC_INVALID_CANCELLED_WhenCalculateDebtPositionOperationTypeThenOk(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setStatus(InstallmentStatus.UNPAID);
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of(installmentDTO, buildInstallmentDTO()));

        SyncCompleteDTO iupdSyncStatusUpdateDTO = new SyncCompleteDTO(InstallmentStatus.UNPAID);
        Map<String, SyncCompleteDTO> iupdSyncStatusUpdateDTOMap = Map.of("iudTest", iupdSyncStatusUpdateDTO);
        // When
        PaymentEventType paymentEventType = resolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertEquals(PaymentEventType.DP_UPDATED, paymentEventType);
    }

    @Test
    void givenNotEvenOneTOSYNCWhenCalculateDebtPositionOperationTypeThenReturnNull(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setStatus(InstallmentStatus.INVALID);
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of(installmentDTO));

        // When
        PaymentEventType paymentEventType = resolver.calculateDebtPositionOperationType(debtPositionDTO, Map.of());

        // Then
        assertNull(paymentEventType);
    }

    @Test
    void givenDoNotContainsIUDWhenCalculateDebtPositionOperationTypeThenReturnNull(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setStatus(InstallmentStatus.TO_SYNC);
        installmentDTO.setIud("iud");
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of(installmentDTO));

        SyncCompleteDTO iupdSyncStatusUpdateDTO = new SyncCompleteDTO(InstallmentStatus.UNPAID);
        Map<String, SyncCompleteDTO> iupdSyncStatusUpdateDTOMap = Map.of("iudTest", iupdSyncStatusUpdateDTO);

        // When
        PaymentEventType paymentEventType = resolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertNull(paymentEventType);
    }

    @Test
    void whenCalculateDebtPositionPaymentEventTypeCreateDPThenOk(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(buildInstallment());

        SyncCompleteDTO iupdSyncStatusUpdateDTO = new SyncCompleteDTO(InstallmentStatus.UNPAID);
        Map<String, SyncCompleteDTO> iupdSyncStatusUpdateDTOMap = Map.of("iud", iupdSyncStatusUpdateDTO, "iud2", iupdSyncStatusUpdateDTO);

        when(installmentOperationTypeResolverMock.calculateInstallmentOperationType(any())).thenReturn(PaymentEventType.DP_CREATED);

        // When
        PaymentEventType paymentEventType = resolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertEquals(PaymentEventType.DP_CREATED, paymentEventType);
    }

    @Test
    void whenCalculateDebtPositionOperationTypeUpdateDPThenOk(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(buildInstallment());

        SyncCompleteDTO iupdSyncStatusUpdateDTO = new SyncCompleteDTO(InstallmentStatus.UNPAID);
        Map<String, SyncCompleteDTO> iupdSyncStatusUpdateDTOMap = Map.of("iud", iupdSyncStatusUpdateDTO, "iud2", iupdSyncStatusUpdateDTO);

        when(installmentOperationTypeResolverMock.calculateInstallmentOperationType(any())).thenReturn(PaymentEventType.DP_UPDATED);

        // When
        PaymentEventType paymentEventType = resolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertEquals(PaymentEventType.DP_UPDATED, paymentEventType);
    }

    @Test
    void whenCalculateDebtPositionOperationTypeDeleteDPThenOk(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(buildInstallment());

        SyncCompleteDTO iupdSyncStatusUpdateDTO = new SyncCompleteDTO(InstallmentStatus.UNPAID);
        Map<String, SyncCompleteDTO> iupdSyncStatusUpdateDTOMap = Map.of("iud", iupdSyncStatusUpdateDTO, "iud2", iupdSyncStatusUpdateDTO);

        when(installmentOperationTypeResolverMock.calculateInstallmentOperationType(any())).thenReturn(PaymentEventType.DP_CANCELLED);

        // When
        PaymentEventType paymentEventType = resolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertEquals(PaymentEventType.DP_CANCELLED, paymentEventType);
    }

    private List<InstallmentDTO> buildInstallment(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setStatus(InstallmentStatus.TO_SYNC);
        installmentDTO.setIud("iud");

        InstallmentDTO installmentDTO2 = new InstallmentDTO();
        installmentDTO2.setStatus(InstallmentStatus.TO_SYNC);
        installmentDTO2.setIud("iud2");

        return List.of(installmentDTO, installmentDTO2);
    }
}
