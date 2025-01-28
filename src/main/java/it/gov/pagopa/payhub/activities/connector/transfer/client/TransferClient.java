package it.gov.pagopa.payhub.activities.connector.transfer.client;

import it.gov.pagopa.payhub.activities.connector.transfer.config.TransferApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class TransferClient {

    private final TransferApisHolder transferApisHolder;

    public TransferClient(TransferApisHolder transferApisHolder) {
        this.transferApisHolder = transferApisHolder;
    }

    public DebtPositionDTO notifyReportedTransferId(String accessToken, Long transferId) {
        return transferApisHolder.getTransferApi(accessToken).notifyReportedTransferId(transferId);
    }
}
