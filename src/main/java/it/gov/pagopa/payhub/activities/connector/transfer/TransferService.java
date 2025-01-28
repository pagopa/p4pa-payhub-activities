package it.gov.pagopa.payhub.activities.connector.transfer;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;

import java.util.Optional;

/**
 * This interface provides methods that manage Transfer of debt positions within the related microservice
 */
public interface TransferService {
	Optional<Transfer> findBySemanticKey(Long orgId, String iuv, String iur, Integer transferIndex);
	DebtPositionDTO notifyReportedTransferId(Long transferId);
}
