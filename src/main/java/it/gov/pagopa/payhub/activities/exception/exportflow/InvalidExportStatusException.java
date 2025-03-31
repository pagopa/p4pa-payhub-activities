package it.gov.pagopa.payhub.activities.exception.exportflow;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

/**
 * Exception indicating that the export file status is invalid and the operation should not be retried.
 * This exception extends {@link NotRetryableActivityException}, signifying that the error is not transient
 * and retrying the operation will likely fail again.
 */
public class InvalidExportStatusException extends NotRetryableActivityException {

    /**
     * Constructs a new InvalidExportStatusException with the specified detail message.
     *
     * @param message The detail message explaining the reason for the exception.
     */
    public InvalidExportStatusException(String message) {
        super(message);
    }
}
