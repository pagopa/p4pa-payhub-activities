package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionClient;
import it.gov.pagopa.payhub.activities.util.DebtPositionUtilities;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

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
    public DebtPositionDTO finalizeSyncStatus(Long debtPositionId, Map<String, IupdSyncStatusUpdateDTO> syncStatusUpdateDTO) {
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
    public String installmentSynchronize(DebtPositionDTO.DebtPositionOriginEnum origin, InstallmentSynchronizeDTO installmentSynchronizeDTO, Boolean massive) {
        String accessToken = authnService.getAccessToken();
        return debtPositionClient.installmentSynchronize(accessToken, origin, installmentSynchronizeDTO, massive);
    }
}
