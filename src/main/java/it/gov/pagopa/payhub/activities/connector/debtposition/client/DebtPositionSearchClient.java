package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPosition;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class DebtPositionSearchClient {

    private final DebtPositionApisHolder debtPositionApisHolder;

    public DebtPositionSearchClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public DebtPosition findById(Long debtPositionId, String accessToken) {
        return debtPositionApisHolder.getDebtPositionSearchControllerApi(accessToken)
                .crudDebtPositionsFindOneWithAllDataByDebtPositionId(debtPositionId);
    }

}
