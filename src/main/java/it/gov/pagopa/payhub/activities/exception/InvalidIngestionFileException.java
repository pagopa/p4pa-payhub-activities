package it.gov.pagopa.payhub.activities.exception;

/**
 * A custom exception that indicates an invalid ingestion file encountered
 * during the application's processing operations.
 *
 * <p>
 * This exception extends {@link ActivitiesException}, representing a specific type
 * of error within the activities-related exception framework.
 * </p>
 *
 * <h2>Purpose</h2>
 * The {@code InvalidIngestionFileException} is thrown when an ingestion file fails validation
 * checks or does not meet the expected criteria. Common causes include:
 * <ul>
 *   <li>The file does not exist at the specified path.</li>
 *   <li>The file is not a regular file (e.g., it could be a directory or a symbolic link).</li>
 *   <li>The file is inaccessible or corrupted.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * Path filePath = Paths.get("/path/to/file.txt");
 * if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
 *     throw new InvalidIngestionFileException("File not found or invalid: " + filePath);
 * }
 * }</pre>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Extends {@link ActivitiesException}, enabling its integration into
 *       the application's activity-related error-handling mechanisms.</li>
 *   <li>Accepts a custom message to provide detailed context about the error.</li>
 * </ul>
 *
 * @see ActivitiesException
 */
public class InvalidIngestionFileException extends ActivitiesException {

	/**
	 * Constructs a new {@code InvalidIngestionFileException} with the specified detail message.
	 *
	 * @param message the detail message explaining the cause of the exception.
	 */
	public InvalidIngestionFileException(String message) {
		super(message);
	}
}

