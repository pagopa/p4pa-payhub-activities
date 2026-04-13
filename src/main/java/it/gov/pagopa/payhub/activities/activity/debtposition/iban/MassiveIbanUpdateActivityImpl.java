package it.gov.pagopa.payhub.activities.activity.debtposition.iban;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionIdViewFilters;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Lazy
@Service
public class MassiveIbanUpdateActivityImpl implements MassiveIbanUpdateActivity {
    private final DebtPositionService debtPositionService;

    public MassiveIbanUpdateActivityImpl(DebtPositionService debtPositionService) {
        this.debtPositionService = debtPositionService;
    }

    @Override
    public Boolean massiveIbanUpdateRetrieveAndUpdateDp(Long orgId, Long dptoId, String oldIban, String newIban, String oldPostalIban, String newPostalIban) {
        DebtPositionIdViewFilters debtPositionIdViewToUpdateFilters = DebtPositionIdViewFilters.builder()
                .organizationId(orgId)
                .dptoId(dptoId)
                .iban(oldIban)
                .postalIban(oldPostalIban)
                .installmentStatuses(List.of(InstallmentStatus.DRAFT, InstallmentStatus.UNPAID, InstallmentStatus.UNPAYABLE, InstallmentStatus.TO_SYNC, InstallmentStatus.EXPIRED))
                .syncError(true)
                .build();

        UpdateTransferIbansAndSyncDebtPositionRequestDTO updateTransferIbansAndSyncDebtPositionRequestDTO = UpdateTransferIbansAndSyncDebtPositionRequestDTO.builder()
                .oldIban(oldIban)
                .newIban(newIban)
                .oldPostalIban(oldPostalIban)
                .newPostalIban(newPostalIban)
                .build();

        List<DebtPositionIdView> debtPositionIdViewsToUpdate;

        do {
            PagedModelDebtPositionIdView pagedModelDebtPositionIdViewToUpdate = debtPositionService.getDebtPositionsIdView(debtPositionIdViewToUpdateFilters, PageRequest.of(0, 100));

            debtPositionIdViewsToUpdate = Optional.ofNullable(pagedModelDebtPositionIdViewToUpdate.getEmbedded())
                    .map(PagedModelDebtPositionIdViewEmbedded::getDebtPositionIdViews)
                    .orElse(Collections.emptyList());

            debtPositionIdViewsToUpdate.forEach(dpIdView -> debtPositionService.updateTransferIbansAndSyncDebtPosition(dpIdView.getDebtPositionId(), updateTransferIbansAndSyncDebtPositionRequestDTO));
        } while(!debtPositionIdViewsToUpdate.isEmpty());

        return checkIfWfIsToReschedule(orgId, dptoId, oldIban, oldPostalIban);
    }

    private boolean checkIfWfIsToReschedule(Long orgId, Long dptoId, String oldIban, String oldPostalIban) {
        DebtPositionIdViewFilters filters = DebtPositionIdViewFilters.builder()
                .organizationId(orgId)
                .dptoId(dptoId)
                .iban(oldIban)
                .postalIban(oldPostalIban)
                .installmentStatuses(List.of(InstallmentStatus.TO_SYNC))
                .syncError(false)
                .build();

        PagedModelDebtPositionIdView pagedModelDebtPositionIdViewInToSync = debtPositionService.getDebtPositionsIdView(filters, PageRequest.of(0, 1));

        List<DebtPositionIdView> debtPositionIdViewsInToSync = Optional.ofNullable(pagedModelDebtPositionIdViewInToSync.getEmbedded())
                .map(PagedModelDebtPositionIdViewEmbedded::getDebtPositionIdViews)
                .orElse(Collections.emptyList());

        return !debtPositionIdViewsInToSync.isEmpty();
    }
}
