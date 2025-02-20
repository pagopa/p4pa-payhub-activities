package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionClient;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentSynchronizeDTOFaker.buildInstallmentSynchronizeDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtPositionServiceTest {

    @Mock
    private DebtPositionClient debtPositionClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private DebtPositionService debtPositionService;

    @BeforeEach
    void setUp() {
        debtPositionService = new DebtPositionServiceImpl(authnServiceMock, debtPositionClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionClientMock,
                authnServiceMock);
    }

    @Test
    void whenFinalizeSyncStatusThenInvokeClient() {
        // Given
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = IupdSyncStatusUpdateDTO.builder()
                .iupdPagopa("iudpPagopa")
                .newStatus(IupdSyncStatusUpdateDTO.NewStatusEnum.TO_SYNC)
                .build();
        String accessToken = "ACCESSTOKEN";

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        debtPositionService.finalizeSyncStatus(0L, Map.of("iud", iupdSyncStatusUpdateDTO));

        // Then
        verify(debtPositionClientMock).finalizeSyncStatus(accessToken, 0L, Map.of("iud", iupdSyncStatusUpdateDTO));
    }

    @Test
    void givenCheckAndUpdateInstallmentExpirationThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentDTO.StatusEnum unpaidStatus = InstallmentDTO.StatusEnum.UNPAID;
        OffsetDateTime now = OffsetDateTime.now();

        InstallmentDTO installment1 = new InstallmentDTO();
        installment1.setStatus(unpaidStatus);
        installment1.setDueDate(now.plusDays(10));

        InstallmentDTO installment2 = new InstallmentDTO();
        installment2.setStatus(unpaidStatus);
        installment2.setDueDate(now.plusDays(5));

        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of(installment1, installment2));

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        when(debtPositionClientMock.checkAndUpdateInstallmentExpiration(accessToken, 1L))
                .thenReturn(debtPositionDTO);

        // When
        OffsetDateTime dueDate = debtPositionService.checkAndUpdateInstallmentExpiration(1L);

        // Then
        assertNotNull(dueDate);
        assertEquals(installment2.getDueDate(), dueDate);
        verify(debtPositionClientMock).checkAndUpdateInstallmentExpiration(accessToken, 1L);
    }

    @Test
    void givenCheckAndUpdateInstallmentExpirationWhenInstallmentIsNotUnpaidThenNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        when(debtPositionClientMock.checkAndUpdateInstallmentExpiration(accessToken,1L))
                .thenReturn(debtPositionDTO);

        // When
        OffsetDateTime dueDate = debtPositionService.checkAndUpdateInstallmentExpiration(1L);

        // Then
        assertNull(dueDate);
        verify(debtPositionClientMock).checkAndUpdateInstallmentExpiration(accessToken,1L);
    }

    @Test
    void givenInstallmentSynchronizeThenReturnsWorkflowId() {
        // Given
        String accessToken = "ACCESSTOKEN";
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        boolean massive = false;
        String expectedWorkflowId = "workflow-123";

        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        when(debtPositionClientMock.installmentSynchronize(accessToken, installmentSynchronizeDTO, massive))
                .thenReturn(expectedWorkflowId);

        // When
        String result = debtPositionService.installmentSynchronize(installmentSynchronizeDTO, massive);

        // Then
        assertEquals(expectedWorkflowId, result);
        verify(debtPositionClientMock).installmentSynchronize(accessToken, installmentSynchronizeDTO, massive);
    }
}
