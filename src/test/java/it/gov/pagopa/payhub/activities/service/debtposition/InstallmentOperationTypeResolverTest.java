package it.gov.pagopa.payhub.activities.service.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSyncStatus;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
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
        PaymentEventType result = resolver.calculateInstallmentOperationType(installment);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenToStatusIsUnpaidAndFromStatusIsDraftThenReturnCreateDP() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentStatus.DRAFT, InstallmentStatus.UNPAID);

        // When
        PaymentEventType result = resolver.calculateInstallmentOperationType(installment);

        // Then
        Assertions.assertEquals(PaymentEventType.DP_CREATED, result);
    }

    @Test
    void whenToStatusIsUnpaidAndFromStatusIsNotDraftThenReturnUpdateDP() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentStatus.UNPAID, InstallmentStatus.UNPAID);

        // When
        PaymentEventType result = resolver.calculateInstallmentOperationType(installment);

        // Then
        Assertions.assertEquals(PaymentEventType.DP_UPDATED, result);
    }

    @Test
    void whenToStatusIsInvalidThenReturnUpdateDP() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentStatus.UNPAID, InstallmentStatus.INVALID);

        // When
        PaymentEventType result = resolver.calculateInstallmentOperationType(installment);

        // Then
        Assertions.assertEquals(PaymentEventType.DP_UPDATED, result);
    }

    @Test
    void whenToStatusIsExpiredThenReturnUpdateDP() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentStatus.UNPAID, InstallmentStatus.EXPIRED);

        // When
        PaymentEventType result = resolver.calculateInstallmentOperationType(installment);

        // Then
        Assertions.assertEquals(PaymentEventType.DP_UPDATED, result);
    }

    @Test
    void whenToStatusIsCancelledThenReturnDeleteDP() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentStatus.UNPAID, InstallmentStatus.CANCELLED);

        // When
        PaymentEventType result = resolver.calculateInstallmentOperationType(installment);

        // Then
        Assertions.assertEquals(PaymentEventType.DP_CANCELLED, result);
    }

    @Test
    void whenToStatusIsUnknownThenReturnNull() {
        // Given
        InstallmentDTO installment = buildInstalmentWithInstallmentSyncStatus(InstallmentStatus.TO_SYNC, InstallmentStatus.PAID);

        // When
        PaymentEventType result = resolver.calculateInstallmentOperationType(installment);

        // Then
        Assertions.assertNull(result);
    }

    private InstallmentDTO buildInstalmentWithInstallmentSyncStatus(InstallmentStatus from, InstallmentStatus to) {
        InstallmentDTO installment = new InstallmentDTO();
        InstallmentSyncStatus syncStatus = new InstallmentSyncStatus();
        syncStatus.setSyncStatusFrom(from);
        syncStatus.setSyncStatusTo(to);
        installment.setSyncStatus(syncStatus);
        return installment;
    }

}
