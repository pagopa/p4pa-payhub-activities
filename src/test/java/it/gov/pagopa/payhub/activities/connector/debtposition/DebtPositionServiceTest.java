package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionClient;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
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
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = IupdSyncStatusUpdateDTO.builder()
                .iupdPagopa("iudpPagopa")
                .newStatus(IupdSyncStatusUpdateDTO.NewStatusEnum.TO_SYNC)
                .build();
        String accessToken = "ACCESSTOKEN";

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);

        // When
        debtPositionService.finalizeSyncStatus(0L, Map.of("iud", iupdSyncStatusUpdateDTO));

        // Then
        Mockito.verify(debtPositionClientMock).finalizeSyncStatus(accessToken, 0L, Map.of("iud", iupdSyncStatusUpdateDTO));
    }

    @Test
    void givenCheckAndUpdateInstallmentExpirationThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        InstallmentDTO.StatusEnum unpaidStatus = InstallmentDTO.StatusEnum.UNPAID;
        LocalDate now = LocalDate.now();

        InstallmentDTO installment1 = new InstallmentDTO();
        installment1.setStatus(unpaidStatus);
        installment1.setDueDate(now.plusDays(10));

        InstallmentDTO installment2 = new InstallmentDTO();
        installment2.setStatus(unpaidStatus);
        installment2.setDueDate(now.plusDays(5));

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
}
