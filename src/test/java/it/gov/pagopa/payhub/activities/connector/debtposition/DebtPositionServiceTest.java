package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionClient;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentSynchronizeDTOFaker.buildInstallmentSynchronizeDTO;
import static it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionOrigin.ORDINARY_SIL;
import static org.junit.jupiter.api.Assertions.*;

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
        Long debtPositionId = 0L;
        SyncStatusUpdateRequestDTO iupdSyncStatusUpdateDTO = new SyncStatusUpdateRequestDTO();
        String accessToken = "ACCESSTOKEN";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        debtPositionService.finalizeSyncStatus(debtPositionId, iupdSyncStatusUpdateDTO);

        // Then
        Mockito.verify(debtPositionClientMock).finalizeSyncStatus(Mockito.same(accessToken), Mockito.same(debtPositionId), Mockito.same(iupdSyncStatusUpdateDTO));
    }

    @Test
    void givenCheckAndUpdateInstallmentExpirationThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentStatus unpaidStatus = InstallmentStatus.UNPAID;
        LocalDate now = LocalDate.now();

        InstallmentDTO installment1 = new InstallmentDTO();
        installment1.setStatus(unpaidStatus);
        installment1.setDueDate(now.plusDays(10));
        installment1.setSwitchToExpired(Boolean.TRUE);

        InstallmentDTO installment2 = new InstallmentDTO();
        installment2.setStatus(unpaidStatus);
        installment2.setDueDate(now.plusDays(5));
        installment1.setSwitchToExpired(Boolean.TRUE);

        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of(installment1, installment2));

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        Mockito.when(debtPositionClientMock.checkAndUpdateInstallmentExpiration(accessToken, 1L))
                .thenReturn(debtPositionDTO);

        // When
        LocalDate dueDate = debtPositionService.checkAndUpdateInstallmentExpiration(1L);

        // Then
        assertNotNull(dueDate);
        assertEquals(installment2.getDueDate(), dueDate);
        Mockito.verify(debtPositionClientMock).checkAndUpdateInstallmentExpiration(accessToken, 1L);
    }

    @Test
    void givenCheckAndUpdateInstallmentExpirationWithSwitchToExpiredFalseThenNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentStatus unpaidStatus = InstallmentStatus.UNPAID;
        LocalDate now = LocalDate.now();

        InstallmentDTO installment1 = new InstallmentDTO();
        installment1.setStatus(unpaidStatus);
        installment1.setDueDate(now.plusDays(10));
        installment1.setSwitchToExpired(Boolean.FALSE);

        debtPositionDTO.getPaymentOptions().getFirst().setInstallments(List.of(installment1));

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        Mockito.when(debtPositionClientMock.checkAndUpdateInstallmentExpiration(accessToken, 1L))
                .thenReturn(debtPositionDTO);

        // When
        LocalDate dueDate = debtPositionService.checkAndUpdateInstallmentExpiration(1L);

        // Then
        assertNull(dueDate);
        Mockito.verify(debtPositionClientMock).checkAndUpdateInstallmentExpiration(accessToken, 1L);
    }

    @Test
    void givenCheckAndUpdateInstallmentExpirationWhenInstallmentIsNotUnpaidThenNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        Mockito.when(debtPositionClientMock.checkAndUpdateInstallmentExpiration(accessToken,1L))
                .thenReturn(debtPositionDTO);

        // When
        LocalDate dueDate = debtPositionService.checkAndUpdateInstallmentExpiration(1L);

        // Then
        assertNull(dueDate);
        Mockito.verify(debtPositionClientMock).checkAndUpdateInstallmentExpiration(accessToken,1L);
    }

    @Test
    void givenInstallmentSynchronizeThenReturnsWorkflowId() {
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionOrigin origin = ORDINARY_SIL;
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        WfExecutionParameters wfExecutionParameters = new WfExecutionParameters();
        String userId = "USERID";
        String expectedWorkflowId = "workflow-123";

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionClientMock.installmentSynchronize(accessToken, origin, installmentSynchronizeDTO, wfExecutionParameters, userId))
                .thenReturn(expectedWorkflowId);

        // When
        String result = debtPositionService.installmentSynchronize(origin, installmentSynchronizeDTO, wfExecutionParameters, userId);

        // Then
        assertEquals(expectedWorkflowId, result);
        Mockito.verify(debtPositionClientMock).installmentSynchronize(accessToken, origin, installmentSynchronizeDTO, wfExecutionParameters, userId);
    }

    @Test
    void givenGetDebtPositionsByIngestionFlowFileIdThenSuccess() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long ingestionFlowFileId = 1L;
        Integer page = 0;
        Integer size = 2;

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionClientMock.getDebtPositionsByIngestionFlowFileId(accessToken, ingestionFlowFileId, null, page, size, null))
                .thenReturn(new PagedDebtPositions());

        // When
        PagedDebtPositions result = debtPositionService.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, null, page, size, null);

        // Then
        assertEquals(new PagedDebtPositions(), result);
    }

    @Test
    void givenUpdateInstallmentNotificationDateRequestWhenUpdateNotificationDateThenSuccess() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String expectedWorkflowId = "workflow-123";
        UpdateInstallmentNotificationDateRequest request = UpdateInstallmentNotificationDateRequest.builder()
                .debtPositionId(1L)
                .nav(Collections.singletonList("nav"))
                .notificationDate(OFFSETDATETIME)
                .build();

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.when(debtPositionClientMock.updateInstallmentNotificationDate(accessToken, request))
                .thenReturn(expectedWorkflowId);

        // When
        debtPositionService.updateInstallmentNotificationDate(request);

        // Then
        Mockito.verify(debtPositionClientMock).updateInstallmentNotificationDate(accessToken, request);
    }

    @Test
    void whenGetDebtPositionsThenSuccess() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionId = 1L;
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionClientMock.getDebtPosition(accessToken, debtPositionId))
                .thenReturn(debtPositionDTO);

        // When
        DebtPositionDTO result = debtPositionService.getDebtPosition(debtPositionId);

        // Then
        assertEquals(debtPositionDTO, result);
    }
}
