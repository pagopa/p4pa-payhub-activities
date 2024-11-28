package it.gov.pagopa.payhub.activities.exception;

/**
 * A custom exception that indicates the absence of an expected ingestion flow
 * in the application's processing logic.
 *
 * <p>
 * This exception extends {@link ActivitiesException}, providing a more specific
 * exception type within the activities-related exception hierarchy.
 * </p>
 *
 * <h2>Purpose</h2>
 * The {@code IngestionFlowNotFoundException} is thrown when an ingestion flow,
 * identified by a specific parameter (e.g., name, ID, or type), cannot be found.
 * This can occur in scenarios such as:
 * <ul>
 *   <li>An unregistered or undefined ingestion flow being accessed.</li>
 *   <li>A misconfiguration in the application's ingestion flow definitions.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * String flowId = "flow123";
 * if (!ingestionFlowExists(flowId)) {
 *     throw new IngestionFlowNotFoundException("Ingestion flow not found: " + flowId);
 * }
 * }</pre>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Extends {@link ActivitiesException}, allowing it to integrate seamlessly into the
 *       application's exception-handling framework for activity-related errors.</li>
 *   <li>Accepts a detailed message to describe the nature and context of the error.</li>
 * </ul>
 *
 * @see ActivitiesException
 */
public class IngestionFlowNotFoundException extends ActivitiesException {

	/**
	 * Constructs a new {@code IngestionFlowNotFoundException} with the specified detail message.
	 *
	 * @param message the detail message explaining the cause of the exception.
	 */
	public IngestionFlowNotFoundException(String message) {
		super(message);
	}
}

