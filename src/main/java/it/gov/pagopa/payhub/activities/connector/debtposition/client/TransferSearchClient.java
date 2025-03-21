package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Set;

@Lazy
@Service
@Slf4j
public class TransferSearchClient {
    private final DebtPositionApisHolder debtPositionApisHolder;

    public TransferSearchClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public Transfer findBySemanticKey(Long orgId, String iuv, String iur, Integer transferIndex, Set<InstallmentStatus> installmentStatusSet, String accessToken) {
        try {
            return debtPositionApisHolder.getTransferSearchControllerApi(accessToken)
                    .crudTransfersFindBySemanticKey(orgId, iuv, iur, transferIndex, installmentStatusSet);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Transfer not found: organizationId: {}, iuv: {}, iur: {}, transferIndex: {}", orgId, iuv, iur, transferIndex);
            return null;
        }
    }
}
