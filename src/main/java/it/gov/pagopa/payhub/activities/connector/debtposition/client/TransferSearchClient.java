package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class TransferSearchClient {
    private final DebtPositionApisHolder debtPositionApisHolder;

	public TransferSearchClient(DebtPositionApisHolder debtPositionApisHolder) {
		this.debtPositionApisHolder = debtPositionApisHolder;
	}

	public Transfer findBySemanticKey(Long orgId, String iuv, String iur, Integer transferIndex, String accessToken) {
        return debtPositionApisHolder.getTransferSearchControllerApi(accessToken)
                .crudTransfersFindBySemanticKey(orgId, iuv, iur, transferIndex, null);
    }
}
