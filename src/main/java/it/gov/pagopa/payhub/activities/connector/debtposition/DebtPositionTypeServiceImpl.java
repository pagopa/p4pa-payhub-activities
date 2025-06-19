package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionTypeClient;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelDebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class DebtPositionTypeServiceImpl implements DebtPositionTypeService {

    private final AuthnService authnService;
    private final DebtPositionTypeClient debtPositionTypeClient;

    public DebtPositionTypeServiceImpl(AuthnService authnService, DebtPositionTypeClient debtPositionTypeClient) {
        this.authnService = authnService;
        this.debtPositionTypeClient = debtPositionTypeClient;
    }


    @Override
    public DebtPositionType createDebtPositionType(DebtPositionTypeRequestBody debtPositionTypeRequestBody) {
        log.debug("Creating a new DebtPositionType with brokerId: {}, code: {} and description: {}",debtPositionTypeRequestBody.getBrokerId(),
                debtPositionTypeRequestBody.getCode(),debtPositionTypeRequestBody.getDescription());
        return debtPositionTypeClient.createDebtPositionType(debtPositionTypeRequestBody, authnService.getAccessToken());
    }

    @Override
    public CollectionModelDebtPositionType getByMainFields(String code, Long brokerId,
        String orgType,
        String macroArea, String serviceType, String collectingReason, String taxonomyCode) {
        return debtPositionTypeClient.getByMainFields(code, brokerId, orgType, macroArea, serviceType,
            collectingReason, taxonomyCode, authnService.getAccessToken());
    }

    @Override
    public CollectionModelDebtPositionType getByBrokerIdAndCode(Long brokerId, String code) {
        return debtPositionTypeClient.getByBrokerIdAndCode(brokerId, code, authnService.getAccessToken());
    }
}
