package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Lazy
@Service
public class DebtPositionClient {

    private final DebtPositionApisHolder debtPositionApisHolder;

    public DebtPositionClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public DebtPositionDTO finalizeSyncStatus(String accessToken, Long debtPositionId, Map<String, IupdSyncStatusUpdateDTO> syncStatusUpdateDTO){
        return debtPositionApisHolder.getDebtPositionApi(accessToken).finalizeSyncStatus(debtPositionId, syncStatusUpdateDTO);
    }

    public DebtPositionDTO checkAndUpdateInstallmentExpiration(String accessToken, Long debtPositionId){
        return debtPositionApisHolder.getDebtPositionApi(accessToken).checkAndUpdateInstallmentExpiration(debtPositionId);
    }

    public String installmentSynchronize(String accessToken, DebtPositionDTO.DebtPositionOriginEnum origin, InstallmentSynchronizeDTO installmentSynchronizeDTO, Boolean massive, String operatorUserId) {
        ResponseEntity<Void> response = debtPositionApisHolder.getDebtPositionApi(accessToken, operatorUserId)
                .installmentSynchronizeWithHttpInfo(origin.getValue(), installmentSynchronizeDTO, massive);

        return response.getHeaders().getFirst("x-workflow-id");
    }

}
