package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;

import java.util.List;

/**
 * Data Access Object (DAO) interface for handling operations related to
 * `Transfer` objects.
 *
 * @see TransferDTO
 */
public interface TransferDao {

	TransferDTO findBySemanticKey(Long orgId, String iuv, String iur, int transferIndex);
}
