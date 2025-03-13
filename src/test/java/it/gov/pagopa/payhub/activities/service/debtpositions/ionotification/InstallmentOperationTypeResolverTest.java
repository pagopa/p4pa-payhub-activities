package it.gov.pagopa.payhub.activities.service.debtpositions.ionotification;

import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.InstallmentOperationTypeResolver;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSyncStatus;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstallmentOperationTypeResolverTest {

    private InstallmentOperationTypeResolver resolver;

    @BeforeEach
    void setUp(){
        resolver = new InstallmentOperationTypeResolver();
    }

    @Test
    void whenSyncStatusIsNullThenReturnNull() {
        // Given
        InstallmentDTO installment = new InstallmentDTO();
        installment.setSyncStatus(null);

        // When
        NotificationRequestDTO.OperationTypeEnum result = resolver.calculateOperationType(installment);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenToStatusIsUnpaidAndFromStatusIsDraftThenReturnCreateDP() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentSyncStatus.SyncStatusFromEnum.DRAFT, InstallmentSyncStatus.SyncStatusToEnum.UNPAID);

        // When
        NotificationRequestDTO.OperationTypeEnum result = resolver.calculateOperationType(installment);

        // Then
        Assertions.assertEquals(NotificationRequestDTO.OperationTypeEnum.CREATE_DP, result);
    }

    @Test
    void whenToStatusIsUnpaidAndFromStatusIsNotDraftThenReturnUpdateDP() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentSyncStatus.SyncStatusFromEnum.UNPAID, InstallmentSyncStatus.SyncStatusToEnum.UNPAID);

        // When
        NotificationRequestDTO.OperationTypeEnum result = resolver.calculateOperationType(installment);

        // Then
        Assertions.assertEquals(NotificationRequestDTO.OperationTypeEnum.UPDATE_DP, result);
    }

    @Test
    void whenToStatusIsInvalidThenReturnUpdateDP() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentSyncStatus.SyncStatusFromEnum.UNPAID, InstallmentSyncStatus.SyncStatusToEnum.INVALID);

        // When
        NotificationRequestDTO.OperationTypeEnum result = resolver.calculateOperationType(installment);

        // Then
        Assertions.assertEquals(NotificationRequestDTO.OperationTypeEnum.UPDATE_DP, result);
    }

    @Test
    void whenToStatusIsExpiredThenReturnUpdateDP() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentSyncStatus.SyncStatusFromEnum.UNPAID, InstallmentSyncStatus.SyncStatusToEnum.EXPIRED);

        // When
        NotificationRequestDTO.OperationTypeEnum result = resolver.calculateOperationType(installment);

        // Then
        Assertions.assertEquals(NotificationRequestDTO.OperationTypeEnum.UPDATE_DP, result);
    }

    @Test
    void whenToStatusIsCancelledThenReturnDeleteDP() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentSyncStatus.SyncStatusFromEnum.UNPAID, InstallmentSyncStatus.SyncStatusToEnum.CANCELLED);

        // When
        NotificationRequestDTO.OperationTypeEnum result = resolver.calculateOperationType(installment);

        // Then
        Assertions.assertEquals(NotificationRequestDTO.OperationTypeEnum.DELETE_DP, result);
    }

    @Test
    void whenToStatusIsUnknownThenReturnNull() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentSyncStatus.SyncStatusFromEnum.TO_SYNC, InstallmentSyncStatus.SyncStatusToEnum.PAID);

        // When
        NotificationRequestDTO.OperationTypeEnum result = resolver.calculateOperationType(installment);

        // Then
        Assertions.assertNull(result);
    }

    private InstallmentDTO buildInstalmentWithInstallmentSyncStatus(InstallmentSyncStatus.SyncStatusFromEnum from, InstallmentSyncStatus.SyncStatusToEnum to) {
        InstallmentDTO installment = new InstallmentDTO();
        InstallmentSyncStatus syncStatus = new InstallmentSyncStatus();
        syncStatus.setSyncStatusFrom(from);
        syncStatus.setSyncStatusTo(to);
        installment.setSyncStatus(syncStatus);
        return installment;
    }

}
