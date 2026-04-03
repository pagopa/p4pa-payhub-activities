package it.gov.pagopa.payhub.activities.activity.debtposition.massive;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionIdViewFilters;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MassiveIbanUpdateActivityTest {
    @Mock
    private DebtPositionService debtPositionServiceMock;

    private MassiveIbanUpdateActivity activity;

    private final Long orgId = 1L;
    private final Long dptoId = 1L;
    private final String oldIban = "oldIban";
    private final String newIban = "newIban";
    private final String oldPostalIban = "oldPostalIban";
    private final String newPostalIban = "newPostalIban";

    private DebtPositionIdViewFilters expectedFilterForUpdate;
    private DebtPositionIdViewFilters expectedFilterForCheck;

    @BeforeEach
    void init() {
        activity = new MassiveIbanUpdateActivityImpl(debtPositionServiceMock);

        expectedFilterForUpdate = DebtPositionIdViewFilters.builder()
                .organizationId(orgId)
                .dptoId(dptoId)
                .iban(oldIban)
                .postalIban(oldPostalIban)
                .installmentStatuses(List.of(InstallmentStatus.DRAFT, InstallmentStatus.UNPAID, InstallmentStatus.UNPAYABLE, InstallmentStatus.TO_SYNC))
                .syncError(true)
                .build();

        expectedFilterForCheck = DebtPositionIdViewFilters.builder()
                .organizationId(orgId)
                .dptoId(dptoId)
                .iban(oldIban)
                .postalIban(oldPostalIban)
                .installmentStatuses(List.of(InstallmentStatus.TO_SYNC))
                .syncError(false)
                .build();
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(debtPositionServiceMock);
    }

    @Test
    void givenNoDebtPositionsToUpdateWhenMassiveIbanUpdateRetrieveAndUpdateDpThenDoNotUpdateAndReturnFalse() {
        Mockito.when(debtPositionServiceMock.getDebtPositionsIdView(
                        expectedFilterForUpdate, PageRequest.of(0, 100)))
                .thenReturn(buildPagedModelDebtPositionIdView());

        Mockito.when(debtPositionServiceMock.getDebtPositionsIdView(
                        expectedFilterForCheck, PageRequest.of(0, 1)))
                .thenReturn(buildPagedModelDebtPositionIdView());

        Boolean result = activity.massiveIbanUpdateRetrieveAndUpdateDp(orgId, dptoId, oldIban, newIban, oldPostalIban, newPostalIban);

        assertFalse(result);
    }

    @Test
    void givenDebtPositionsToUpdateWhenMassiveIbanUpdateRetrieveAndUpdateDpThenUpdateAndReturnTrue() {
        UpdateTransferIbansAndSyncDebtPositionRequestDTO updateTransferIbansAndSyncDebtPositionRequestDTO = UpdateTransferIbansAndSyncDebtPositionRequestDTO.builder()
                .oldIban(oldIban)
                .newIban(newIban)
                .oldPostalIban(oldPostalIban)
                .newPostalIban(newPostalIban)
                .build();

        Mockito.when(debtPositionServiceMock.getDebtPositionsIdView(
                        expectedFilterForUpdate, PageRequest.of(0, 100)))
                .thenReturn(buildPagedModelDebtPositionIdView(1L, 2L))
                .thenReturn(buildPagedModelDebtPositionIdView());

        Mockito.when(debtPositionServiceMock.getDebtPositionsIdView(
                        expectedFilterForCheck, PageRequest.of(0, 1)))
                .thenReturn(buildPagedModelDebtPositionIdView(3L));

        Mockito.doNothing().when(debtPositionServiceMock).updateTransferIbansAndSyncDebtPosition(
                1L, updateTransferIbansAndSyncDebtPositionRequestDTO);
        Mockito.doNothing().when(debtPositionServiceMock).updateTransferIbansAndSyncDebtPosition(
               2L, updateTransferIbansAndSyncDebtPositionRequestDTO);

        Boolean result = activity.massiveIbanUpdateRetrieveAndUpdateDp(orgId, dptoId, oldIban, newIban, oldPostalIban, newPostalIban);

        assertTrue(result);
    }

    @Test
    void givenDebtPositionsToUpdateWhenMassiveIbanUpdateRetrieveAndUpdateDpThenUpdateAndReturnFalse() {
        UpdateTransferIbansAndSyncDebtPositionRequestDTO updateTransferIbansAndSyncDebtPositionRequestDTO = UpdateTransferIbansAndSyncDebtPositionRequestDTO.builder()
                .oldIban(oldIban)
                .newIban(newIban)
                .oldPostalIban(oldPostalIban)
                .newPostalIban(newPostalIban)
                .build();

        Mockito.when(debtPositionServiceMock.getDebtPositionsIdView(
                        expectedFilterForUpdate, PageRequest.of(0, 100)))
                .thenReturn(buildPagedModelDebtPositionIdView(1L))
                .thenReturn(buildPagedModelDebtPositionIdView());

        Mockito.when(debtPositionServiceMock.getDebtPositionsIdView(
                        expectedFilterForCheck, PageRequest.of(0, 1)))
                .thenReturn(buildPagedModelDebtPositionIdView());

        Mockito.doNothing().when(debtPositionServiceMock).updateTransferIbansAndSyncDebtPosition(
                1L, updateTransferIbansAndSyncDebtPositionRequestDTO);

        Boolean result = activity.massiveIbanUpdateRetrieveAndUpdateDp(orgId, dptoId, oldIban, newIban, oldPostalIban, newPostalIban);

        assertFalse(result);
    }

    private PagedModelDebtPositionIdView buildPagedModelDebtPositionIdView(Long... ids) {
        List<DebtPositionIdView> debtPositionIdViews = ids.length == 0
                ? Collections.emptyList()
                : Arrays.stream(ids)
                    .map(id -> DebtPositionIdView.builder().debtPositionId(id).build())
                    .collect(Collectors.toList());

        return PagedModelDebtPositionIdView.builder()
                .embedded(PagedModelDebtPositionIdViewEmbedded.builder()
                        .debtPositionIdViews(debtPositionIdViews)
                        .build())
                .build();
    }
}
