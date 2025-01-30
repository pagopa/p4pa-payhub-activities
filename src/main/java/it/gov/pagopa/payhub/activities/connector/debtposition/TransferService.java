package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;

/**
 * This interface provides methods that manage Transfer of debt positions within the related microservice
 */
public interface TransferService {
	Transfer findBySemanticKey(TransferSemanticKeyDTO transferSemanticKey);
	DebtPositionDTO notifyReportedTransferId(Long transferId);
}
