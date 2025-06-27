package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionClient;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.payhub.activities.util.DebtPositionUtilities;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Lazy
@Service
public class DebtPositionServiceImpl implements DebtPositionService {

    private final AuthnService authnService;
    private final DebtPositionClient debtPositionClient;

    public DebtPositionServiceImpl(AuthnService authnService, DebtPositionClient debtPositionClient) {
        this.authnService = authnService;
        this.debtPositionClient = debtPositionClient;
    }

    @Override
    public DebtPositionDTO finalizeSyncStatus(Long debtPositionId, SyncStatusUpdateRequestDTO syncStatusUpdateDTO) {
        String accessToken = authnService.getAccessToken();
        return debtPositionClient.finalizeSyncStatus(accessToken, debtPositionId, syncStatusUpdateDTO);
    }

    @Override
    public LocalDate checkAndUpdateInstallmentExpiration(Long debtPositionId) {
        String accessToken = authnService.getAccessToken();
        DebtPositionDTO debtPositionDTO = debtPositionClient.checkAndUpdateInstallmentExpiration(accessToken, debtPositionId);

        return DebtPositionUtilities.calcDebtPositionNextDueDate(debtPositionDTO);
    }

    @Override
    public String installmentSynchronize(DebtPositionOrigin origin, InstallmentSynchronizeDTO installmentSynchronizeDTO, WfExecutionParameters wfExecutionParameters, String operatorUserId) {
        String accessToken = authnService.getAccessToken();
        return debtPositionClient.installmentSynchronize(accessToken, origin, installmentSynchronizeDTO, wfExecutionParameters, operatorUserId);
    }

    @Override
    public PagedDebtPositions getDebtPositionsByIngestionFlowFileId(Long ingestionFlowFileId, List<InstallmentStatus> statusToExclude, Integer page, Integer size, List<String> sort) {
        String accessToken = authnService.getAccessToken();
        return debtPositionClient.getDebtPositionsByIngestionFlowFileId(accessToken, ingestionFlowFileId, statusToExclude, page, size, sort);
    }

    @Override
    public void updateInstallmentNotificationDate(UpdateInstallmentNotificationDateRequest updateInstallmentNotificationDateRequest) {
        String accessToken = authnService.getAccessToken();
        debtPositionClient.updateInstallmentNotificationDate(accessToken, updateInstallmentNotificationDateRequest);
    }

    @Override
    public DebtPositionDTO getDebtPosition(Long debtPositionId) {
        String accessToken = authnService.getAccessToken();
        return debtPositionClient.getDebtPosition(accessToken, debtPositionId);
    }
}
