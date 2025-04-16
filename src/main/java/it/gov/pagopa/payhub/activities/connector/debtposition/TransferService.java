package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelTransfer;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferReportedRequest;
import java.util.Set;

/**
 * This interface provides methods that manage Transfer of debt positions within the related microservice
 */
public interface TransferService {
	Transfer findBySemanticKey(TransferSemanticKeyDTO transferSemanticKey, Set<InstallmentStatus> installmentStatusSet);
	DebtPositionDTO notifyReportedTransferId(Long transferId, TransferReportedRequest request);
	CollectionModelTransfer findByInstallmentId(Long installmentId);
}
