package it.gov.pagopa.payhub.activities.service.debtpositions.ionotification;

import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.DebtOperationOperationTypeResolver;
import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.InstallmentOperationTypeResolver;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO.OperationTypeEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtOperationOperationTypeResolverTest {

    @Mock
    private InstallmentOperationTypeResolver installmentOperationTypeResolverMock;

    private DebtOperationOperationTypeResolver resolver;

    @BeforeEach
    void setUp(){
        resolver = new DebtOperationOperationTypeResolver(installmentOperationTypeResolverMock);
    }



    @Test
    void givenCalculateDebtPositionOperationTypeWhenStatusNotEvenOne_TOSYNC_INVALID_CANCELLED_ThenOk(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setStatus(InstallmentDTO.StatusEnum.UNPAID);
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of(installmentDTO, buildInstallmentDTO()));

        // When
        NotificationRequestDTO.OperationTypeEnum operationType = resolver.calculateDebtPositionOperationType(debtPositionDTO, Map.of());

        // Then
        assertEquals(UPDATE_DP, operationType);
    }

    @Test
    void givenCalculateDebtPositionOperationTypeWhenNotEvenOneTOSYNCThenReturnNull(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setStatus(InstallmentDTO.StatusEnum.INVALID);
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of(installmentDTO));

        // When
        NotificationRequestDTO.OperationTypeEnum operationType = resolver.calculateDebtPositionOperationType(debtPositionDTO, Map.of());

        // Then
        assertNull(operationType);
    }

    @Test
    void givenCalculateDebtPositionOperationTypeWhenDoNotContainsIUDThenReturnNull(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setStatus(InstallmentDTO.StatusEnum.TO_SYNC);
        installmentDTO.setIud("iud");
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of(installmentDTO));

        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(IupdSyncStatusUpdateDTO.NewStatusEnum.UNPAID, "iupdPagopa");
        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = Map.of("iudTest", iupdSyncStatusUpdateDTO);

        // When
        NotificationRequestDTO.OperationTypeEnum operationType = resolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertNull(operationType);
    }

    @Test
    void givenCalculateDebtPositionOperationTypeCreateDPThenOk(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(buildInstallment());

        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(IupdSyncStatusUpdateDTO.NewStatusEnum.UNPAID, "iupdPagopa");
        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = Map.of("iud", iupdSyncStatusUpdateDTO, "iud2", iupdSyncStatusUpdateDTO);

        when(installmentOperationTypeResolverMock.calculateInstallmentOperationType(any())).thenReturn(CREATE_DP);

        // When
        NotificationRequestDTO.OperationTypeEnum operationType = resolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertEquals(CREATE_DP, operationType);
    }

    @Test
    void givenCalculateDebtPositionOperationTypeUpdateDPThenOk(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(buildInstallment());

        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(IupdSyncStatusUpdateDTO.NewStatusEnum.UNPAID, "iupdPagopa");
        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = Map.of("iud", iupdSyncStatusUpdateDTO, "iud2", iupdSyncStatusUpdateDTO);

        when(installmentOperationTypeResolverMock.calculateInstallmentOperationType(any())).thenReturn(UPDATE_DP);

        // When
        NotificationRequestDTO.OperationTypeEnum operationType = resolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertEquals(UPDATE_DP, operationType);
    }

    @Test
    void givenCalculateDebtPositionOperationTypeDeleteDPThenOk(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(buildInstallment());

        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO =
                new IupdSyncStatusUpdateDTO(IupdSyncStatusUpdateDTO.NewStatusEnum.UNPAID, "iupdPagopa");
        Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap = Map.of("iud", iupdSyncStatusUpdateDTO, "iud2", iupdSyncStatusUpdateDTO);

        when(installmentOperationTypeResolverMock.calculateInstallmentOperationType(any())).thenReturn(DELETE_DP);

        // When
        NotificationRequestDTO.OperationTypeEnum operationType = resolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        // Then
        assertEquals(DELETE_DP, operationType);
    }

    private List<InstallmentDTO> buildInstallment(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setStatus(InstallmentDTO.StatusEnum.TO_SYNC);
        installmentDTO.setIud("iud");

        InstallmentDTO installmentDTO2 = new InstallmentDTO();
        installmentDTO2.setStatus(InstallmentDTO.StatusEnum.TO_SYNC);
        installmentDTO2.setIud("iud2");

        return List.of(installmentDTO, installmentDTO2);
    }
}
