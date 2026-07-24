package it.gov.pagopa.payhub.activities.activity.debtposition.iban;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionIdViewFilters;
import it.gov.pagopa.payhub.activities.util.ThreadUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Lazy
@Service
public class MassiveIbanUpdateActivityImpl implements MassiveIbanUpdateActivity {
    private final int maxConcurrentRequests;

    private final DebtPositionService debtPositionService;

    public MassiveIbanUpdateActivityImpl(
            DebtPositionService debtPositionService,
            @Value("${massive-iban-update.max-concurrent-requests}") int maxConcurrentRequests
    ) {
        this.debtPositionService = debtPositionService;
        this.maxConcurrentRequests = maxConcurrentRequests;
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

        ActivityExecutionContext activityContext = Activity.getExecutionContext();
        int totalProcessed = activityContext.getHeartbeatDetails(Integer.class).orElse(0);

        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            do {
                PagedModelDebtPositionIdView pagedModelDebtPositionIdViewToUpdate = debtPositionService.getDebtPositionsIdView(debtPositionIdViewToUpdateFilters, PageRequest.of(0, 500));

                debtPositionIdViewsToUpdate = Optional.ofNullable(pagedModelDebtPositionIdViewToUpdate.getEmbedded())
                        .map(PagedModelDebtPositionIdViewEmbedded::getDebtPositionIdViews)
                        .orElse(Collections.emptyList());

                if (!debtPositionIdViewsToUpdate.isEmpty()) {
                    List<List<DebtPositionIdView>> batches = ListUtils.partition(debtPositionIdViewsToUpdate, maxConcurrentRequests);

                    for (List<DebtPositionIdView> batch : batches) {
                        processBatch(batch, updateTransferIbansAndSyncDebtPositionRequestDTO, executorService, mdcContextMap);
                    }

                    totalProcessed += debtPositionIdViewsToUpdate.size();
                    activityContext.heartbeat(totalProcessed);
                }
            } while (!debtPositionIdViewsToUpdate.isEmpty());
        }

        return checkIfWfIsToReschedule(orgId, dptoId, oldIban, oldPostalIban);
    }

    private void processBatch(
            List<DebtPositionIdView> batch,
            UpdateTransferIbansAndSyncDebtPositionRequestDTO updateTransferIbansAndSyncDebtPositionRequestDTO,
            ExecutorService executorService,
            Map<String, String> mdcContextMap
    ) {
        List<Future<?>> futures = new ArrayList<>(batch.size());

        for (DebtPositionIdView dpIdView : batch) {
            futures.add(
                    ThreadUtils.submit(executorService, () ->
                                    debtPositionService.updateTransferIbansAndSyncDebtPosition(
                                            dpIdView.getDebtPositionId(),
                                            updateTransferIbansAndSyncDebtPositionRequestDTO
                                    ),
                            mdcContextMap
                    )
            );
        }

        awaitAll(futures);
    }

    private void awaitAll(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
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
