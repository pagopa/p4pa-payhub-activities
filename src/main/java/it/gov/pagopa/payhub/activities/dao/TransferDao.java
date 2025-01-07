package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;

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
	 * @param orgId  organization id
	 * @param iuv    payment identifier
	 * @param iur    reporting identifier
	 * @param transferIndex transfer index
	 * @return TransferDTO object returned
	 */
	TransferDTO findBySemanticKey(Long orgId, String iuv, String iur, int transferIndex);
}
