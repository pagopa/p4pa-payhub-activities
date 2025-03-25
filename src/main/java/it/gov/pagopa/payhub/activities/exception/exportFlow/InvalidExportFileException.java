package it.gov.pagopa.payhub.activities.exception.exportFlow;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

/**
 * A custom exception that indicates an invalid file encountered during the export process
 * in the application's processing logic.
 *
 */
public class InvalidExportFileException extends NotRetryableActivityException {

    /**
     * Constructs a new {@code InvalidExportFileException} with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception.
     */
    public InvalidExportFileException(String message) {
        super(message);
    }
}