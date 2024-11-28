package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.ingestionflow.IngestionFlowDTO;

import java.util.Optional;

/**
 * Data Access Object (DAO) interface for managing ingestion flows.
 *
 * <p>
 * The {@code IngestionFlowDao} provides an abstraction for accessing and manipulating
 * ingestion flow data. It focuses on retrieving ingestion flow details from the underlying
 * data source using defined operations.
 * </p>
 *
 * <h2>Purpose</h2>
 * The interface enables the separation of the data access layer from the business logic layer,
 * promoting modularity and testability. It is commonly implemented by a class that interacts
 * with a database or other persistent storage mechanism.
 *
 * <h2>Method Details</h2>
 * <ul>
 *   <li>
 *     <strong>{@code getIngestionFlow(Long ingestionFlowId)}</strong>:
 *     Fetches an {@link IngestionFlowDTO} entity using its unique identifier.
 *     <ul>
 *       <li>Parameter: {@code ingestionFlowId} - The unique ID of the ingestion flow to retrieve.</li>
 *       <li>Returns: {@code Optional<IngestionFlowDTO>} - An optional containing the ingestion flow data, or empty if not found.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Defines a clear contract for ingestion flow data retrieval.</li>
 *   <li>Supports returning results in an {@link Optional} to handle absent data gracefully.</li>
 *   <li>Encourages a clean and maintainable design in data access operations.</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Assuming IngestionFlowDao is implemented by a class IngestionFlowDaoImpl
 * IngestionFlowDao ingestionFlowDao = new IngestionFlowDaoImpl();
 * Long ingestionFlowId = 123L;
 * Optional<IngestionFlowDTO> flow = ingestionFlowDao.getIngestionFlow(ingestionFlowId);
 *
 * flow.ifPresent(f -> System.out.println("Ingestion Flow Retrieved: " + f));
 * }</pre>
 *
 * @see IngestionFlowDTO
 */
public interface IngestionFlowDao {

	/**
	 * Retrieves the {@link IngestionFlowDTO} entity based on the provided ingestion flow ID.
	 *
	 * @param ingestionFlowId the unique identifier of the ingestion flow to be retrieved.
	 * @return an {@link Optional} containing the requested {@link IngestionFlowDTO} if found,
	 *         or an empty {@link Optional} if no matching entity exists.
	 */
	Optional<IngestionFlowDTO> getIngestionFlow(Long ingestionFlowId);
}

