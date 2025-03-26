package it.gov.pagopa.payhub.activities.exception.exportflow;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

/**
 * A custom exception that indicates the absence of an expected export
 * in the application's processing logic.
 *
 */
public class ExportFileNotFoundException extends NotRetryableActivityException {

	/**
	 * Constructs a new {@code ExportFileFileNotFoundException} with the specified detail message.
	 *
	 * @param message the detail message explaining the cause of the exception.
	 */
	public ExportFileNotFoundException(String message) {super(message);
	}
}

