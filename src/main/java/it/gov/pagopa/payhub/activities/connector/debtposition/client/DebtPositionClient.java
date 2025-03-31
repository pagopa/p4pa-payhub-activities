package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public String installmentSynchronize(String accessToken, DebtPositionOrigin origin, InstallmentSynchronizeDTO installmentSynchronizeDTO, WfExecutionParameters wfExecutionParameters, String operatorUserId) {
        ResponseEntity<Void> response = debtPositionApisHolder.getDebtPositionApi(accessToken, operatorUserId)
                .installmentSynchronizeWithHttpInfo(origin, installmentSynchronizeDTO, wfExecutionParameters.isMassive(), wfExecutionParameters.isPartialChange());

        return response.getHeaders().getFirst("x-workflow-id");
    }

    public PagedDebtPositions getDebtPositionsByIngestionFlowFileId(String accessToken, Long ingestionFlowFileId, Integer page, Integer size, List<String> sort){
        return debtPositionApisHolder.getDebtPositionApi(accessToken).getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, page, size, sort);
    }

    public String updateInstallmentNotificationDate(String accessToken, UpdateInstallmentNotificationDateRequest updateInstallmentNotificationDateRequest) {
        ResponseEntity<Void> response = debtPositionApisHolder.getDebtPositionApi(accessToken)
                .updateInstallmentNotificationDateWithHttpInfo(updateInstallmentNotificationDateRequest);

        return response.getHeaders().getFirst("x-workflow-id");
    }

}
