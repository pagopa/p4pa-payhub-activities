package it.gov.pagopa.payhub.activities.connector.transfer;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.transfer.client.TransferClient;
import it.gov.pagopa.payhub.activities.connector.transfer.client.TransferSearchClient;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
	public Optional<Transfer> findBySemanticKey(TransferSemanticKeyDTO transferSemanticKey) {
		return Optional.ofNullable(transferSearchClient.findBySemanticKey(
				transferSemanticKey.getOrgId(),
				transferSemanticKey.getIuv(),
				transferSemanticKey.getIur(),
				transferSemanticKey.getTransferIndex(),
				authnService.getAccessToken()
			)
		);
	}

	@Override
	public DebtPositionDTO notifyReportedTransferId(Long transferId) {
		return transferClient.notifyReportedTransferId(authnService.getAccessToken(), transferId);
	}
}
