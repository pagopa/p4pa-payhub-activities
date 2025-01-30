package it.gov.pagopa.payhub.activities.exception.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

/**
 * A custom exception that indicates an invalid ingestion file encountered
 * during the application's processing operations.
 *
 */
public class InvalidIngestionFileException extends NotRetryableActivityException {

	/**
	 * Constructs a new {@code InvalidIngestionFileException} with the specified detail message.
	 *
	 * @param message the detail message explaining the cause of the exception.
	 */
	public InvalidIngestionFileException(String message) {
		super(message);
	}
}

