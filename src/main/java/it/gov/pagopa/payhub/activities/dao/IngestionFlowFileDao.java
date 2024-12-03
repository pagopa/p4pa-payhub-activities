package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;

import java.util.Optional;

/**
 * Data Access Object (DAO) interface for managing ingestion flows.
 *
 * <p>
 * The {@code IngestionFlowFileDao} provides an abstraction for accessing and manipulating
 * ingestion flow data. It focuses on retrieving ingestion flow details from the underlying
 * data source using defined operations.
 * </p>
 *
 * @see IngestionFlowFileDTO
 */
public interface IngestionFlowFileDao {

	/**
	 * Retrieves the {@link IngestionFlowFileDTO} entity based on the provided ingestion flow ID.
	 *
	 * @param ingestionFlowId the unique identifier of the ingestion flow to be retrieved.
	 * @return an {@link Optional} containing the requested {@link IngestionFlowFileDTO} if found,
	 *         or an empty {@link Optional} if no matching entity exists.
	 */
	Optional<IngestionFlowFileDTO> getIngestionFlow(Long ingestionFlowId);
}

