package it.gov.pagopa.payhub.activities.exception.exportflow;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

/**
 * A custom exception that indicates the unsupported file type encountered during the export flow
 * in the application's processing logic.
 *
 */
public class ExportFileTypeNotSupportedException extends NotRetryableActivityException {

    /**
     * Constructs a new {@code ExportFlowFileTypeNotSupported} with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception.
     */
    public ExportFileTypeNotSupportedException(String message) {
        super("EXPORT_FILE_NOT_SUPPORTED", message);
    }
}
