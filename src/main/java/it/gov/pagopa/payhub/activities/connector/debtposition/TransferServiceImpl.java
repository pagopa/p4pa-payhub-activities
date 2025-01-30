package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.TransferClient;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.TransferSearchClient;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class TransferServiceImpl implements TransferService {
    private final AuthnService authnService;
    private final TransferClient transferClient;
    private final TransferSearchClient transferSearchClient;

    public TransferServiceImpl(AuthnService authnService, TransferClient transferClient, TransferSearchClient transferSearchClient) {
        this.authnService = authnService;
        this.transferClient = transferClient;
        this.transferSearchClient = transferSearchClient;
    }

    @Override
    public Transfer findBySemanticKey(TransferSemanticKeyDTO transferSemanticKey) {
        return transferSearchClient.findBySemanticKey(
                transferSemanticKey.getOrgId(),
                transferSemanticKey.getIuv(),
                transferSemanticKey.getIur(),
                transferSemanticKey.getTransferIndex(),
                authnService.getAccessToken()
        );
    }

    @Override
    public DebtPositionDTO notifyReportedTransferId(Long transferId) {
        return transferClient.notifyReportedTransferId(authnService.getAccessToken(), transferId);
    }
}
