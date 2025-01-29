package it.gov.pagopa.payhub.activities.connector.transfer;

import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;

import java.util.Optional;

/**
 * This interface provides methods that manage Transfer of debt positions within the related microservice
 */
public interface TransferService {
	Optional<Transfer> findBySemanticKey(TransferSemanticKeyDTO transferSemanticKey);
	DebtPositionDTO notifyReportedTransferId(Long transferId);
}
