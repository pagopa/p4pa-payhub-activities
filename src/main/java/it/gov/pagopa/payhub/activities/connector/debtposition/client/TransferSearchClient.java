package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Set;
import java.util.stream.Collectors;

@Lazy
@Service
@Slf4j
public class TransferSearchClient {
    private final DebtPositionApisHolder debtPositionApisHolder;

    public TransferSearchClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public Transfer findBySemanticKey(Long orgId, String iuv, String iur, Integer transferIndex, Set<InstallmentNoPII.StatusEnum> installmentStatusSet, String accessToken) {
        Set<String> installmentStatusSetString = installmentStatusSet.stream().map(Enum::name).collect(Collectors.toSet());
        try {
            return debtPositionApisHolder.getTransferSearchControllerApi(accessToken)
                    .crudTransfersFindBySemanticKey(orgId, iuv, iur, transferIndex, installmentStatusSetString);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Transfer not found: organizationId: {}, iuv: {}, iur: {}, transferIndex: {}", orgId, iuv, iur, transferIndex);
            return null;
        }
    }
}
