package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPosition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Slf4j
@Service
public class DebtPositionSearchClient {

    private final DebtPositionApisHolder debtPositionApisHolder;

    public DebtPositionSearchClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public DebtPosition findById(Long debtPositionId, String accessToken) {
        try{
            return debtPositionApisHolder.getDebtPositionEntityControllerApi(accessToken)
                    .crudGetDebtposition(String.valueOf(debtPositionId));
        } catch (RestInvokeNotFoundException e){
            log.info("Cannot find DebtPosition having id {}", debtPositionId);
            return null;
        }
    }

}
