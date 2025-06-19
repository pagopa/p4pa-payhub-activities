package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelDebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class DebtPositionTypeClient {

    private final DebtPositionApisHolder debtPositionApisHolder;

    public DebtPositionTypeClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public DebtPositionType createDebtPositionType(
        DebtPositionTypeRequestBody debtPositionTypeRequestBody, String accessToken) {
            return debtPositionApisHolder.getDebtPositionTypeEntityControllerApi(accessToken)
                    .crudCreateDebtpositiontype(debtPositionTypeRequestBody);
    }

    public CollectionModelDebtPositionType getByMainFields(String code, Long brokerId, String orgType,
        String macroArea, String serviceType, String collectingReason, String taxonomyCode, String accessToken) {
        return debtPositionApisHolder.getDebtPositionTypeSearchControllerApi(accessToken)
            .crudDebtPositionTypesFindByMainFields(code, brokerId, orgType, macroArea, serviceType,
                collectingReason, taxonomyCode);
    }

    public CollectionModelDebtPositionType getByBrokerIdAndCode(Long brokerId, String code, String accessToken) {
        return debtPositionApisHolder.getDebtPositionTypeSearchControllerApi(accessToken)
                .crudDebtPositionTypesFindByBrokerIdAndCode(brokerId,code);
    }


}
