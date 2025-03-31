package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferReportedRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class TransferClient {
    private final DebtPositionApisHolder debtPositionApisHolder;

    public TransferClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public DebtPositionDTO notifyReportedTransferId(String accessToken, Long transferId, TransferReportedRequest request) {
        return debtPositionApisHolder.getTransferApi(accessToken).notifyReportedTransferId(transferId, request);
    }
}
