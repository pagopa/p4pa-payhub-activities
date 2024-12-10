package it.gov.pagopa.payhub.activities.exception;

/**
 * A custom exception that indicates the absence of an expected ingestion flow
 * in the application's processing logic.
 *
 */
public class PaymentsClassificatioSaveException extends ActivitiesException {

	/**
	 * Constructs a new {@code PaymentsClassificatioSaveException} with the specified detail message.
	 *
	 * @param message the detail message explaining the cause of the exception.
	 */
	public PaymentsClassificatioSaveException(String message) {
		super(message);
	}
}

