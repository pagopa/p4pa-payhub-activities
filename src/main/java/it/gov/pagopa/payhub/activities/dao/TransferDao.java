package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;

/**
 * Data Access Object (DAO) interface for handling operations related to
 * `Transfer` objects.
 *
 * @see TransferDTO
 */
public interface TransferDao {

	/**
	 * find transfer by semantic key
	 *
	 * @param transferSemanticKeyDTO the DTO containing semantic keys such as organization ID, IUV, IUR, and transfer index.
	 * @return TransferDTO object returned
	 */
	TransferDTO findBySemanticKey(TransferSemanticKeyDTO transferSemanticKeyDTO);
}
