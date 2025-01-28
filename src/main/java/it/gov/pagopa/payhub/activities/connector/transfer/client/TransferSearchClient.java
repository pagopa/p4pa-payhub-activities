package it.gov.pagopa.payhub.activities.connector.transfer.client;

import it.gov.pagopa.payhub.activities.connector.transfer.config.TransferApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class TransferSearchClient {

    private final TransferApisHolder transferApisHolder;

    public TransferSearchClient(TransferApisHolder transferApisHolder) {
        this.transferApisHolder = transferApisHolder;
    }

    public Transfer findBySemanticKey(Long orgId, String iuv, String iur, Integer transferIndex, String accessToken) {
        return transferApisHolder.getTransferSearchControllerApi(accessToken)
                .crudTransfersFindBySemanticKey(orgId, iuv, iur, transferIndex);
    }
}
